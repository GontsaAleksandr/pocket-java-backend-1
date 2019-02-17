#https://docs.mongodb.com/manual/reference/method/

mongod

mongo --host localhost --port 27017


use pocketdb

db.pocketdb.roles.insertMany([{"name":'ROLE_ADMIN'},{"name":'ROLE_USER'}])

db.pocketdb.users.insertOne({"name":'test', "password":'123', "lastname":'testev', "firstname":'test', "email":'t@t.com'}, "roles":[{"name":'ROLE_ADMIN'},{"name":'ROLE_USER'}])



