use admin
db.createUser(
   {
     user: "mongodb",
     pwd: "mongodb",
     roles: [ ]
   }
)
#Create Administrative User with Roles
db.createUser(
   {
     user: "pocket",
     pwd: "pocket",
     roles:
       [
         { role: "readWrite", db: "config" },
         "clusterAdmin"
       ]
   }
)

use pocketdb
db.createUser( { user: "pocket",
                 pwd: "pocket",
                 customData: { employeeId: 12345 },
                 roles: [ { role: "clusterAdmin", db: "admin" },
                          { role: "readAnyDatabase", db: "admin" },
                          "readWrite"] },
               { w: "majority" , wtimeout: 5000 } )