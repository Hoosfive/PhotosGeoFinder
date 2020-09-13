import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.photos.Photo;

import javax.swing.*;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

public class GetPhotos implements Callable {

    private final Double lat;
    private final Double lon;
    private final int rad;
    private int count;
    private final UserActor actor;
    private TransportClient transportClient = HttpTransportClient.getInstance();
    private VkApiClient vk = new VkApiClient(transportClient);

    GetPhotos(String lat, String lon, String rad, int count) {
        this.lat = Double.valueOf(lat);
        this.lon = Double.valueOf(lon);
        this.rad = Integer.parseInt(rad);
        this.count = count;
        actor = Config.readConfig();
    }

    @Override
    public Object call() {
        Vector<MyPhoto> photosList = new Vector<>();
        List<Photo> photoList;
        String photoUrl, ownerUrl, groupPhotoUrl;
        try {
            photoList = vk.photos().search(actor)
                    .lat(lat)
                    .lng(lon)
                    .radius(rad)
                    .count(count)
                    .execute()
                    .getItems();

            if (count > photoList.size())
                count = photoList.size();
            for (int i = 0; i < count; i++) {
                try {
                    Photo el = photoList.get(i);
                    if (el.getOwnerId() > 0) {
                        photoUrl = String.format("https://vk.com/id%d?z=photo%d_%d%%2Fphotos%d", el.getOwnerId(), el.getOwnerId(), el.getId(), el.getOwnerId());
                        groupPhotoUrl = null;
                        ownerUrl = "https://vk.com/id" + el.getOwnerId();
                    } else {
                        groupPhotoUrl = String.format("https://vk.com/club%d?z=photo%d_%d%%2Falbum%d_%d%%2Frev", -el.getOwnerId(), el.getOwnerId(), el.getId(), el.getOwnerId(), el.getAlbumId());
                        photoUrl = String.format("https://vk.com/public%d?z=photo%d_%d%%2Falbum%d_%d%%2Frev", -el.getOwnerId(), el.getOwnerId(), el.getId(), el.getOwnerId(), el.getAlbumId());
                        ownerUrl = "https://vk.com/club" + -el.getOwnerId();
                    }
                    photosList.addElement(new MyPhoto(el.getOwnerId(),
                            ownerUrl,
                            photoUrl,
                            groupPhotoUrl,
                            el.getLat(),
                            el.getLng()));
                } catch (Exception e) {
                    //   System.out.println("shit, id " + i + " are fucking my app");
                }
            }
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
            changeToken();
        }

        return photosList;
    }

    static void changeToken() {
        String actor = JOptionPane.showInputDialog(null,
                new String[]{"Введите свой id и access token (vkhost.github.io, разрешение 'Фотографии'):", "Ввод через запятую, пример: '\"34239487,а320ар229...\"'"},
                "Измените данные и запустите поиск ещё раз",
                JOptionPane.WARNING_MESSAGE);
        int id = Integer.parseInt(actor.substring(0, actor.indexOf(",")));
        String token = actor.substring(actor.indexOf(",") + 1);
        Config.writeConfig(new UserActor(id, token));
    }
}
