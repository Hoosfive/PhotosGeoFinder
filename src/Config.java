import com.vk.api.sdk.client.actors.UserActor;

import java.io.*;

class Config {
    private static final File config = new File("photoFinderConfig.txt");

    static UserActor readConfig() {
        try {
            createConfig();
            BufferedReader br = new BufferedReader(new FileReader(config));
            String tempId = br.readLine();
            int id = 0;
            if (tempId != null)
                id = Integer.parseInt(tempId);
            String token = br.readLine();
            br.close();
            if (token != null && !token.isEmpty())
                return new UserActor(id, token);
            else return null;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при чтении конфига");
            return null;
        }
    }

    static void writeConfig(UserActor actor) {
        try {
            createConfig();
            BufferedWriter bw = new BufferedWriter(new FileWriter(config));
            bw.write(actor.getId() + "\n");
            bw.write(actor.getAccessToken());
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при записи в конфиг");
        }
    }

    private static void createConfig() {
        try {
            if (!config.exists()) {
                if (config.createNewFile())
                    System.out.println("Файл был успешно создан");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
