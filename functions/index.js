const functions = require("firebase-functions");

var admin = require("firebase-admin");
var serviceAccount = require("./moimingrelease-firebase-adminsdk-8ordb-0ed3f2b047.json");


admin.initializeApp({

    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://moimingrelease-default-rtdb.firebaseio.com/"

});

const kakaoRequestMeUrl = 'https://kapi.kakao.com/v2/user/me?sercure_resource=true'
const axios = require('axios').default;

function requestMe(kakaoAccessToken, callback) {
    console.log('Requesting user profile from Kakao API server.')
    
    return axios.get(kakaoRequestMeUrl, {
        method: 'GET',
        headers: {'Authorization': 'Bearer' + kakaoAccessToken}
    }).then((result)=>{

      console.log('Result: ', result.data)

      callback(null, result.data, result)

    }).catch((error)=>{

      alert('Error', error.response)

    });
  }

/**
   * updateOrCreateUser - Update Firebase user with the give email, create if
   * none exists.
   *
   * @param  {String} userId        user id per app
   * @param  {String} email         user's email address
   * @param  {String} displayName   user
   * @param  {String} photoURL      profile photo url
   * @return {Promise<UserRecord>} Firebase user record in a promise
   */
 function updateOrCreateUser(oauth_provider, userId, email, displayName, photoURL) {
    console.log('updating or creating a firebase user');

    const updateParams = {
        provider: oauth_provider,
        displayName: displayName,
        email: email,
        photoURL: photoURL
    };

    if (displayName) {
      updateParams['displayName'] = displayName;
    }
    if (email) {
      updateParams['email'] = email;
    }
    if (photoURL) {
      updateParams['photoURL'] = photoURL;
    }

    console.log(updateParams);
    return admin.auth().updateUser(userId, updateParams)
    .catch((error) => {
      if (error.code === 'auth/user-not-found') { // 유저를 새로 생성해야 한다면.

        updateParams['uid'] = userId;

        if (email) {
          updateParams['email'] = email;
        }

        if(photoURL){
          updateParams['photoURL'] = photoURL;
        }

        return admin.auth().createUser(updateParams);
      }
      throw error;
    });
  }






function createFirebaseToken(oauth_provider, kakaoAccessToken){

    return requestMe(kakaoAccessToken).then((response) => {
        const body = JSON.parse(response)
        console.log(body)
        const userId = `kakao:${body.id}`
        if (!userId) {
          return res.status(404)
          .send({message: 'There was no user with the given access token.'})
        }

        let nickname = null
        let profileImage = null
        
        if (body.properties) {
            nickname = body.properties.nickname
            profileImage = body.properties.profile_image_url
          }
          return updateOrCreateUser(oauth_provider, userId, body.kakao_account.email, nickname,
            profileImage)
        }).then((userRecord) => {
          const userId = userRecord.uid
          console.log(`creating a custom firebase token based on uid ${userId}`)
          return admin.auth().createCustomToken(userId, {provider: oauth_provider})
        })
}

exports.kakaoCustomAuth = functions.region('asia-northeast3').https
.onRequest((req, res) => {
  const token = req.body.token
  if (!token) return resp.status(400).send({error: 'There is no kakao token.'})
  .send({message: 'Access token is a required parameter.'})

  console.log(`Verifying Kakao token: ${token}`)
  createFirebaseToken('KAKAO', token).then((firebaseToken) => {
    console.log(`Returning firebase token to user: ${firebaseToken}`)
    res.send({firebase_token: firebaseToken});
  })

  return
})