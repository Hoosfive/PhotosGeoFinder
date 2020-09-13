class MyPhoto {
    int ownerID;
    String ownerUrl;
    String photoURl;
    String groupPhotoUrl;
    double latitude;
    double longitude;

    MyPhoto(int ownerID, String ownerUrl, String photoURl, String groupPhotoUrl, double latitude, double longitude) {
        this.ownerID = ownerID;
        this.ownerUrl = ownerUrl;
        this.photoURl = photoURl;
        this.groupPhotoUrl = groupPhotoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
