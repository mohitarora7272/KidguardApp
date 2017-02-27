package com.kidguard.pojo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "status",
        "error",
        "message",
        "user"
})
public class LogInPOJO {

    @JsonProperty("status")
    private Integer status;
    @JsonProperty("error")
    private Boolean error;
    @JsonProperty("message")
    private String message;
    @JsonProperty("user")
    private User user;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("error")
    public Boolean getError() {
        return error;
    }

    @JsonProperty("error")
    public void setError(Boolean error) {
        this.error = error;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("user")
    public User getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(User user) {
        this.user = user;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public class User {

        @JsonProperty("id")
        private Integer id;
        @JsonProperty("email")
        private String email;
        @JsonProperty("device_code")
        private String device_code;
        @JsonProperty("access_token")
        private String access_token;
        @JsonProperty("encrypted_password")
        private String encrypted_password;
        @JsonProperty("first_name")
        private String first_name;
        @JsonProperty("last_name")
        private String last_name;
        @JsonProperty("type")
        private String type;
        @JsonProperty("user_id")
        private String user_id;
        @JsonProperty("external_id")
        private String external_id;
        @JsonProperty("mac_address")
        private String mac_address;
        @JsonProperty("device_registration_id")
        private String device_registration_id;
        @JsonProperty("active_subscriber")
        private String active_subscriber;
        @JsonProperty("limited_trial_end_at")
        private Object limited_trial_end_at;
        @JsonProperty("current_referral_code")
        private String current_referral_code;
        @JsonProperty("current_used_coupon_code")
        private Object current_used_coupon_code;
        @JsonProperty("created_at")
        private String created_at;
        @JsonProperty("updated_at")
        private String updated_at;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("id")
        public Integer getId() {
            return id;
        }

        @JsonProperty("id")
        public void setId(Integer id) {
            this.id = id;
        }

        @JsonProperty("email")
        public String getEmail() {
            return email;
        }

        @JsonProperty("email")
        public void setEmail(String email) {
            this.email = email;
        }

        @JsonProperty("device_code")
        public String getDeviceCode() {
            return device_code;
        }

        @JsonProperty("device_code")
        public void setDeviceCode(String device_code) {
            this.device_code = device_code;
        }

        @JsonProperty("access_token")
        public String getAccessToken() {
            return access_token;
        }

        @JsonProperty("access_token")
        public void setAccessToken(String access_token) {
            this.access_token = access_token;
        }

        @JsonProperty("encrypted_password")
        public String getEncryptedPassword() {
            return encrypted_password;
        }

        @JsonProperty("encrypted_password")
        public void setEncryptedPassword(String encrypted_password) {
            this.encrypted_password = encrypted_password;
        }

        @JsonProperty("first_name")
        public String getFirstName() {
            return first_name;
        }

        @JsonProperty("first_name")
        public void setFirstName(String first_name) {
            this.first_name = first_name;
        }

        @JsonProperty("last_name")
        public String getLastName() {
            return last_name;
        }

        @JsonProperty("last_name")
        public void setLastName(String last_name) {
            this.last_name = last_name;
        }

        @JsonProperty("type")
        public String getType() {
            return type;
        }

        @JsonProperty("type")
        public void setType(String type) {
            this.type = type;
        }

        @JsonProperty("user_id")
        public String getUserId() {
            return user_id;
        }

        @JsonProperty("user_id")
        public void setUserId(String user_id) {
            this.user_id = user_id;
        }

        @JsonProperty("external_id")
        public String getExternalId() {
            return external_id;
        }

        @JsonProperty("external_id")
        public void setExternalId(String external_id) {
            this.external_id = external_id;
        }

        @JsonProperty("mac_address")
        public String getMacAddress() {
            return mac_address;
        }

        @JsonProperty("mac_address")
        public void setMacAddress(String mac_address) {
            this.mac_address = mac_address;
        }

        @JsonProperty("device_registration_id")
        public String getDeviceRegistrationId() {
            return device_registration_id;
        }

        @JsonProperty("device_registration_id")
        public void setDeviceRegistrationId(String device_registration_id) {
            this.device_registration_id = device_registration_id;
        }

        @JsonProperty("active_subscriber")
        public String getActiveSubscriber() {
            return active_subscriber;
        }

        @JsonProperty("active_subscriber")
        public void setActiveSubscriber(String active_subscriber) {
            this.active_subscriber = active_subscriber;
        }

        @JsonProperty("limited_trial_end_at")
        public Object getLimitedTrialEndAt() {
            return limited_trial_end_at;
        }

        @JsonProperty("limited_trial_end_at")
        public void setLimitedTrialEndAt(Object limited_trial_end_at) {
            this.limited_trial_end_at = limited_trial_end_at;
        }

        @JsonProperty("current_referral_code")
        public String getCurrentReferralCode() {
            return current_referral_code;
        }

        @JsonProperty("current_referral_code")
        public void setCurrentReferralCode(String current_referral_code) {
            this.current_referral_code = current_referral_code;
        }

        @JsonProperty("current_used_coupon_code")
        public Object getCurrentUsedCouponCode() {
            return current_used_coupon_code;
        }

        @JsonProperty("current_used_coupon_code")
        public void setCurrentUsedCouponCode(Object current_used_coupon_code) {
            this.current_used_coupon_code = current_used_coupon_code;
        }

        @JsonProperty("created_at")
        public String getCreatedAt() {
            return created_at;
        }

        @JsonProperty("created_at")
        public void setCreatedAt(String created_at) {
            this.created_at = created_at;
        }

        @JsonProperty("updated_at")
        public String getUpdatedAt() {
            return updated_at;
        }

        @JsonProperty("updated_at")
        public void setUpdatedAt(String updated_at) {
            this.updated_at = updated_at;
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
