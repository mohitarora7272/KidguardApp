package com.kidguard.pojo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "success",
        "message",
        "data"
})
@SuppressWarnings("all")
public class LogInPOJO {

    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("message")
    private String message;
    @JsonProperty("data")
    private List<Datum> data = new ArrayList<Datum>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The success
     */
    @JsonProperty("success")
    public Boolean getSuccess() {
        return success;
    }

    /**
     * @param success The success
     */
    @JsonProperty("success")
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * @return The message
     */
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return The data
     */
    @JsonProperty("data")
    public List<Datum> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    @JsonProperty("data")
    public void setData(List<Datum> data) {
        this.data = data;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "id",
            "username",
            "password",
            "email",
            "country",
            "gender",
            "image",
            "date_created",
            "date_updated",
            "latitude",
            "longitude",
            "status",
            "device_token",
            "total_photos",
            "memory_given",
            "checkin_time",
            "checkout_time"
    })
    public class Datum {

        @JsonProperty("id")
        private String id;
        @JsonProperty("username")
        private String username;
        @JsonProperty("password")
        private String password;
        @JsonProperty("email")
        private String email;
        @JsonProperty("country")
        private String country;
        @JsonProperty("gender")
        private String gender;
        @JsonProperty("image")
        private String image;
        @JsonProperty("date_created")
        private String date_created;
        @JsonProperty("date_updated")
        private String date_updated;
        @JsonProperty("latitude")
        private String latitude;
        @JsonProperty("longitude")
        private String longitude;
        @JsonProperty("status")
        private String status;
        @JsonProperty("device_token")
        private String device_token;
        @JsonProperty("total_photos")
        private String total_photos;
        @JsonProperty("memory_given")
        private String memory_given;
        @JsonProperty("checkin_time")
        private String checkin_time;
        @JsonProperty("checkout_time")
        private String checkout_time;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         * @return The id
         */
        @JsonProperty("id")
        public String getId() {
            return id;
        }

        /**
         * @param id The id
         */
        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return The username
         */
        @JsonProperty("username")
        public String getUsername() {
            return username;
        }

        /**
         * @param username The username
         */
        @JsonProperty("username")
        public void setUsername(String username) {
            this.username = username;
        }

        /**
         * @return The password
         */
        @JsonProperty("password")
        public String getPassword() {
            return password;
        }

        /**
         * @param password The password
         */
        @JsonProperty("password")
        public void setPassword(String password) {
            this.password = password;
        }

        /**
         * @return The email
         */
        @JsonProperty("email")
        public String getEmail() {
            return email;
        }

        /**
         * @param email The email
         */
        @JsonProperty("email")
        public void setEmail(String email) {
            this.email = email;
        }

        /**
         * @return The country
         */
        @JsonProperty("country")
        public String getCountry() {
            return country;
        }

        /**
         * @param country The country
         */
        @JsonProperty("country")
        public void setCountry(String country) {
            this.country = country;
        }

        /**
         * @return The gender
         */
        @JsonProperty("gender")
        public String getGender() {
            return gender;
        }

        /**
         * @param gender The gender
         */
        @JsonProperty("gender")
        public void setGender(String gender) {
            this.gender = gender;
        }

        /**
         * @return The image
         */
        @JsonProperty("image")
        public String getImage() {
            return image;
        }

        /**
         * @param image The image
         */
        @JsonProperty("image")
        public void setImage(String image) {
            this.image = image;
        }

        /**
         * @return The dateCreated
         */
        @JsonProperty("date_created")
        public String getDateCreated() {
            return date_created;
        }

        /**
         * @param dateCreated The date_created
         */
        @JsonProperty("date_created")
        public void setDateCreated(String date_created) {
            this.date_created = date_created;
        }

        /**
         * @return The dateUpdated
         */
        @JsonProperty("date_updated")
        public String getDateUpdated() {
            return date_updated;
        }

        /**
         * @param dateUpdated The date_updated
         */
        @JsonProperty("date_updated")
        public void setDateUpdated(String date_updated) {
            this.date_updated = date_updated;
        }

        /**
         * @return The latitude
         */
        @JsonProperty("latitude")
        public String getLatitude() {
            return latitude;
        }

        /**
         * @param latitude The latitude
         */
        @JsonProperty("latitude")
        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        /**
         * @return The longitude
         */
        @JsonProperty("longitude")
        public String getLongitude() {
            return longitude;
        }

        /**
         * @param longitude The longitude
         */
        @JsonProperty("longitude")
        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        /**
         * @return The status
         */
        @JsonProperty("status")
        public String getStatus() {
            return status;
        }

        /**
         * @param status The status
         */
        @JsonProperty("status")
        public void setStatus(String status) {
            this.status = status;
        }

        /**
         * @return The deviceToken
         */
        @JsonProperty("device_token")
        public String getDeviceToken() {
            return device_token;
        }

        /**
         * @param deviceToken The device_token
         */
        @JsonProperty("device_token")
        public void setDeviceToken(String device_token) {
            this.device_token = device_token;
        }

        /**
         * @return The totalPhotos
         */
        @JsonProperty("total_photos")
        public String getTotalPhotos() {
            return total_photos;
        }

        /**
         * @param totalPhotos The total_photos
         */
        @JsonProperty("total_photos")
        public void setTotalPhotos(String total_photos) {
            this.total_photos = total_photos;
        }

        /**
         * @return The memoryGiven
         */
        @JsonProperty("memory_given")
        public String getMemoryGiven() {
            return memory_given;
        }

        /**
         * @param memoryGiven The memory_given
         */
        @JsonProperty("memory_given")
        public void setMemoryGiven(String memory_given) {
            this.memory_given = memory_given;
        }

        /**
         * @return The checkinTime
         */
        @JsonProperty("checkin_time")
        public String getCheckinTime() {
            return checkin_time;
        }

        /**
         * @param checkinTime The checkin_time
         */
        @JsonProperty("checkin_time")
        public void setCheckinTime(String checkin_time) {
            this.checkin_time = checkin_time;
        }

        /**
         * @return The checkoutTime
         */
        @JsonProperty("checkout_time")
        public String getCheckoutTime() {
            return checkout_time;
        }

        /**
         * @param checkoutTime The checkout_time
         */
        @JsonProperty("checkout_time")
        public void setCheckoutTime(String checkout_time) {
            this.checkout_time = checkout_time;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }

}

