package ca.sfu.djlin.walkinggroup.proxy;

import java.util.List;

import ca.sfu.djlin.walkinggroup.dataobjects.GpsLocation;
import ca.sfu.djlin.walkinggroup.dataobjects.Group;
import ca.sfu.djlin.walkinggroup.dataobjects.PermissionRequest;
import ca.sfu.djlin.walkinggroup.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * The ProxyBuilder class will handle the apiKey and token being injected as a header to all calls
 * This is a Retrofit interface.
 */
public interface WGServerProxy {
    @GET("getApiKey")
    Call<String> getApiKey(@Query("groupName") String groupName);

    // -----------------------------
    // Users
    // -----------------------------
    @POST("/users/signup")
    Call<User> createUser(@Body User user);

    @POST("/login")
    Call<Void> login(@Body User userWithEmailAndPassword);

    @GET("/users")
    Call<List<User>> getUsers();

    @GET("/users/{id}")
    Call<User> getUserById(@Path("id") Long userId);

    @GET("/users/{id}")
    Call<User> getUserById(@Path("id") Long userId, @Header("JSON-DEPTH") Long depth);

    @GET("/users/byEmail")
    Call<User> getUserByEmail(@Query("email") String email);

    @DELETE("/users/{id}")
    Call<Void> deleteUser(@Path("id") Long userId);

    // -----------------------------
    // GPS Location
    // -----------------------------
    @GET("/users/{id}/lastGpsLocation")
    Call<GpsLocation> getLastGpsLocation(@Path("id") Long userId);

    @POST("/users/{id}/lastGpsLocation")
    Call<GpsLocation> setLastGpsLocation(@Path("id") Long userId, @Body GpsLocation location);


    // -----------------------------
    // User Monitoring
    // -----------------------------
    @GET("/users/{id}/monitoredByUsers")
    Call<List<User>> getMonitoredByUsers(@Path("id") Long userId);

    @POST("/users/{id}/monitoredByUsers")
    Call<List<User>> addToMonitoredByUsers(@Path("id") Long userId, @Body User user);

    @DELETE("/users/{id}/monitoredByUsers/{childId}")
    Call<Void> removeFromMonitoredByUsers(@Path("id") Long userId, @Path("childId") Long childId);


    @GET("/users/{id}/monitorsUsers")
    Call<List<User>> getMonitorsUsers(@Path("id") Long userId);

    @POST("/users/{id}/monitorsUsers")
    Call<List<User>> addToMonitorsUsers(@Path("id") Long userId, @Body User user);

    @DELETE("/users/{id}/monitorsUsers/{childId}")
    Call<Void> removeFromMonitorsUsers(@Path("id") Long userId, @Path("childId") Long childId);


    // -----------------------------
    // Groups
    // -----------------------------
    @GET("/groups")
    Call<List<Group>> getGroups();

    @GET("/groups/{id}")
    Call<Group> getGroupById(@Path("id") Long groupId);

    @POST("/groups")
    Call<Group> createGroup(@Body Group group);

    @POST("/groups/{id}")
    Call<Group> updateGroup(@Path("id") Long groupId, @Body Group group);

    @DELETE("/groups/{id}")
    Call<Void> deleteGroup(@Path("id") Long groupId);

    @GET("/groups/{id}/memberUsers")
    Call<List<User>> getGroupMembers(@Path("id") Long groupId);

    @POST("/groups/{id}/memberUsers")
    Call<List<User>> addGroupMember(@Path("id") Long groupId, @Body User user);

    @DELETE("/groups/{id}/memberUsers/{userId}")
    Call<Void> removeGroupMember(@Path("id") Long groupId, @Path("userId") Long userId);


    // -----------------------------
    // Messages
    // -----------------------------
    @POST("/messages/togroup/{groupId}")
    Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> newMessageToGroup(@Path("groupId") Long groupId, @Body ca.cmpt276.walkinggroup.dataobjects.Message message);

    @POST("/messages/toparentsof/{userId}")
    Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> newMessageToParentsOf(@Path("userId") Long userId, @Body ca.cmpt276.walkinggroup.dataobjects.Message message);

    @GET("/messages")
    Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> getMessages();
    @GET("/messages")
    Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> getMessages(@Query("touser") Long toUserId);
    @GET("/messages")
    Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> getMessages(@Query("touser") Long toUserId,  @Header("JSON-DEPTH") Long depth);
    @GET("/messages")
    Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> getMessages(@Query("touser") Long toUserId, @Query("is-emergency") Boolean isEmergency);
    @GET("/messages?status=unread")
    Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> getUnreadMessages(
            @Query("touser") Long toUserId,
            @Query("is-emergency") Boolean isEmergency);    // null for not filtering
    @GET("/messages?status=read")
    Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> getReadMessages(
            @Query("touser") Long toUserId,
            @Query("is-emergency") Boolean isEmergency);    // null for not filtering

    @GET("/messages/{id}")
    Call<ca.cmpt276.walkinggroup.dataobjects.Message> getOneMessage(@Path("id") Long id);

    // if markAsRead is false, then marks it as unread.
    @POST("/messages/{id}/mark-read-or-unread")
    Call<ca.cmpt276.walkinggroup.dataobjects.Message> markMessageAsRead(@Path("id") Long id, @Body Boolean markAsRead);

    @DELETE("/messages/{id}")
    Call<Void> deleteMessage(@Path("id") Long id);

    // -----------------------------
    // Permissions
    // -----------------------------
    // TODO: Add query options
    @GET("/permissions")
    Call<List<PermissionRequest>> getPermissions();

    @GET("/permissions")
    Call<List<PermissionRequest>> getPermissions(@Query("userId") Long userId);

    @GET("/permissions")
    Call<List<PermissionRequest>> getPermissions(@Query("userId") Long userId, @Query("statusForUser") PermissionStatus status);

    @GET("/permissions/{id}")
    Call<PermissionRequest> getPermissionById(@Path("id") long permissionId);

    @POST("/permissions/{id}")
    Call<PermissionRequest> approveOrDenyPermissionRequest(
            @Path("id") long permissionId,
            @Body PermissionStatus status
    );

    // -- Internal --
    @GET("/permissions/actions")
    Call<List<ActionInfo>> getPermissionActionInfo();
    //  -- Internal --
    @POST("/permissions/{id}/magicOverrideDoNotUse")
    Call<PermissionRequest> magicOverridePermissionById(
            @Path("id") long permissionId,
            @Body PermissionRequest request
    );

    //--Edit--
    @POST("/users/{id}")
    Call<User> editUser(@Path("id") Long userId, @Body User user);

    // -----------------------------
    // Small data classes required by permissions
    // -----------------------------
    class ActionInfo {
        String value;
        String description;
    }

    enum PermissionStatus {
        PENDING,
        APPROVED,
        DENIED
    }


}
