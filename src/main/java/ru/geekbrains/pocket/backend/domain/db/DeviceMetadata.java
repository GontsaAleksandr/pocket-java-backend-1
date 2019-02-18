//package ru.geekbrains.pocket.backend.domain.db;
//
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.bson.types.ObjectId;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.util.Date;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@Document(collection = "device_meta_data")
//public class DeviceMetadata {
//
//    @Id
//    private ObjectId id;
//    private ObjectId userId;
//    private String deviceDetails;
//    private String location;
//    private Date lastLoggedIn;
//
//    @Override
//    public String toString() {
//        final StringBuilder sb = new StringBuilder("DeviceMetadata{");
//        sb.append("id=").append(id);
//        sb.append(", userId=").append(userId);
//        sb.append(", deviceDetails='").append(deviceDetails).append('\'');
//        sb.append(", location='").append(location).append('\'');
//        sb.append(", lastLoggedIn=").append(lastLoggedIn);
//        sb.append('}');
//        return sb.toString();
//    }
//
////    @Override
////    public boolean equals(Object o) {
////        if (this == o) return true;
////        if (o == null || getClass() != o.getClass()) return false;
////        DeviceMetadata that = (DeviceMetadata) o;
////        return Objects.equals(id, that.id) &&
////                Objects.equals(userId, that.userId) &&
////                Objects.equals(deviceDetails, that.deviceDetails) &&
////                Objects.equals(location, that.location) &&
////                Objects.equals(lastLoggedIn, that.lastLoggedIn);
////    }
////
////    @Override
////    public int hashCode() {
////        return Objects.hash(id, userId, deviceDetails, location, lastLoggedIn);
////    }
//}
