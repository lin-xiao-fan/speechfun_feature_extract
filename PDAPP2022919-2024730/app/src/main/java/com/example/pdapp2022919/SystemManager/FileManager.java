//package com.example.pdapp2022919.SystemManager;
//
//import android.media.MediaMetadataRetriever;
//import android.os.Build;
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import androidx.annotation.NonNull;
//
//import com.example.pdapp2022919.Profile.ProfileData;
//import com.example.pdapp2022919.Recode.RecordData;
//import com.example.pdapp2022919.net.Client;
//import com.google.gson.Gson;
//
//import java.io.File;
//import java.io.FileFilter;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.FilenameFilter;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//public class FileManager {
//
//    public enum FileType {
//
//        GAME(0,"game"),
//        SHORT_LINE(1,"shortLine"),
//        PROFILE(2,"profile"),
//        QUESTIONNAIRE(3,"questionnaire");
//
//        final String displayName;
//        final int order;
//
//        FileType(int order, String displayName) {
//            this.order = order;
//            this.displayName = displayName;
//        }
//
//        static FileType getType(int i) {
//            switch (i) {
//                case 0:
//                    return GAME;
//                case 1:
//                    return SHORT_LINE;
//                case 2:
//                    return PROFILE;
//                default:
//                    return null;
//            }
//        }
//
//    }
//
//    public static class HistoryData implements Parcelable {
//        public final Date date;
//        public final FileType type;
//
//        private HistoryData(FileType type, Date date) {
//            this.type = type;
//            this.date = date;
//        }
//
//        protected HistoryData(Parcel in) {
//            date = new Date(in.readLong());
//            type = FileType.getType(in.readInt());
//        }
//
//        public static final Creator<HistoryData> CREATOR = new Creator<HistoryData>() {
//            @Override
//            public HistoryData createFromParcel(Parcel in) {
//                return new HistoryData(in);
//            }
//
//            @Override
//            public HistoryData[] newArray(int size) {
//                return new HistoryData[size];
//            }
//        };
//
//        @Override
//        public int describeContents() {
//            return 0;
//        }
//
//        @Override
//        public void writeToParcel(@NonNull Parcel parcel, int i) {
//            parcel.writeLong(date.getTime());
//            parcel.writeInt(type.order);
//        }
//    }
//
//    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
//    private static final int READ_BUFFER_LIMIT = 1024;
//
//    private static String fileDir;
//    private static String gameFilePrefix;
//    private static String profilePrefix;
//    private static String shortLineFilePrefix;
//    private static String questionnaireFilePrefix;
//    private static String gameFolder;
//    private static String shortLineFolder;
//    private static String profileFolder;
//    private static String questionnaireFolder;
//
//    public static void setFileDir(String path) {
//        fileDir = path;
//    }
//
//    public static String getFileDir() {
//        return fileDir;
//    }
//
//    public static void setTimestamp(FileType type) {
//        String timestamp = Long.toString(System.currentTimeMillis());
//        String uuid = Client.getUuid().toString();
//        switch (type) {
//            case GAME:
//                gameFilePrefix = String.join("_", new String[] {
//                        type.displayName, timestamp
//                });
//                gameFolder = String.join("/", new String[] {
//                        fileDir, uuid, type.displayName, timestamp
//                });
//                checkFolder(gameFolder);
//                break;
//            case SHORT_LINE:
//                shortLineFilePrefix = String.join("_", new String[] {
//                    type.displayName, timestamp
//            });
//                shortLineFolder = String.join("/", new String[] {
//                        fileDir, uuid, type.displayName, timestamp
//                });
//                checkFolder(shortLineFolder);
//                break;
//            case PROFILE:
//                profilePrefix = String.join("_", new String[] {
//                        type.displayName
//                });
//                profileFolder = String.join("/", new String[] {
//                        fileDir, uuid, type.displayName
//                });
//                checkFolder(profileFolder);
//                break;
//            case QUESTIONNAIRE:
//                questionnaireFilePrefix = String.join("_", new String[] {
//                        type.displayName, timestamp
//                });
//                questionnaireFolder = String.join("/", new String[] {
//                        fileDir, uuid, type.displayName
//                });
//                checkFolder(questionnaireFolder);
//                break;
//        }
//    }
//
//    public static File getGameFile(int level, int life) {
//        String fileName = gameFilePrefix + "_game" + level + "_" + life + ".wav";
//        return new File(gameFolder, fileName);
//    }
//
//    public static File getPreTestFile() {
//        return new File(gameFolder, gameFilePrefix + "_preTest.wav");
//    }
//
//    public static File getPostTestFile() {
//        return new File(gameFolder, gameFilePrefix + "_postTest.wav");
//    }
//
//    public static File getProfile() {
//        return new File(profileFolder, profilePrefix + "_profile.json");
//    }
//
//    public static void writeProfile(ProfileData data) {
//        File profile = new File(profileFolder, profilePrefix + "_profile.json");
//        System.out.println(profile.getAbsolutePath());
//        String gson = new Gson().toJson(data);
//        try {
//            FileOutputStream os = new FileOutputStream(profile);
//            os.write(gson.getBytes());
//        } catch (IOException exp) {
//            exp.printStackTrace();
//        }
//    }
//
//    public static ProfileData readProfile() {
//        File profile = new File(profileFolder, profilePrefix + "_profile.json");
//        System.out.println(profile.getAbsolutePath());
//        if (!profile.exists()) {
//            ProfileData file = new ProfileData();
//            file.patient_name = "Guest";
//            file.patient_number = "123456789";
//            return file;
//        }
//        StringBuilder builder = new StringBuilder();
//        try {
//            FileInputStream is = new FileInputStream(profile);
//            int ava = is.available();// 確認有多少東西可以讀
//            while (ava > 0) {//如果讀的東西大於零
//                byte[] bytes = new byte[Math.min(ava, READ_BUFFER_LIMIT)];//取最小值，限制季檔案讀取大小，避免記憶體不足，最大1024
//                is.read(bytes);
//                builder.append(new String(bytes));//把讀到的東西轉為字串，並接在讀到的東西後面
//                ava = is.available();//更新可以讀取的數量
//            }
//        } catch (IOException exp) {   //確保讀取是否崩潰，如果崩潰將顯示在run
//            exp.printStackTrace();
//        }
//        return new Gson().fromJson(builder.toString(), ProfileData.class);//回傳profile data(gson把String轉為Class)
//    }
//
//    public static void writeHistoryFile(RecordData data) {
//        File history = new File(gameFolder, gameFilePrefix + "_history.json");
//        String gson = new Gson().toJson(data);
//        try {
//            FileOutputStream os = new FileOutputStream(history);
//            os.write(gson.getBytes());
//        } catch (IOException exp) {
//            exp.printStackTrace();
//        }
//    }
//
//    public static RecordData readHistoryFile(HistoryData data) {
//        File[] files = getFiles(data, (file, name) -> name.endsWith("history.json"));
//        if (files == null || files.length < 1) return null;
//        System.out.println(files.length);
//        try {
//            FileReader reader = new FileReader(files[0]);
//            return new Gson().fromJson(reader, RecordData.class);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static File getShortLineFile() {
//        return new File(shortLineFolder, shortLineFilePrefix + "_shortLine.wav");
//    }
//
//    public static String getShortLinePath(Date date) {
//        String fileType = FileType.SHORT_LINE.displayName;
//        String time = Long.toString(date.getTime());
//        String fileName = String.join("_", new String[] {
//                fileType, time, "shortLine.wav"
//        });
//        return String.join("/", new String[] {
//                fileDir, Client.getUuid().toString(), fileType, time, fileName
//        });
//    }
//
//    public static File getVHI_10_File() {
//        return new File(questionnaireFolder, questionnaireFilePrefix + "_vhi_10.json");
//    }
//
//    public static File getVOS_File() {
//        return new File(questionnaireFolder, questionnaireFilePrefix + "_vos.json");
//    }
//
////   q
//
//    public static File[] getFiles(HistoryData data) {
//        return getFiles(data, null);
//    }
//
//    public static File[] getFiles(HistoryData data, FilenameFilter filter) {
//        String path = String.join("/", new String[] {
//                fileDir, Client.getUuid().toString(), data.type.displayName, Long.toString(data.date.getTime())
//        });
//        File folder = new File(path);
//        if (!folder.exists()) return null;
//        if (filter == null) return folder.listFiles();
//        return folder.listFiles(filter);
//    }
//
//    //把所有的紀錄放在日曆中，需所有有遊玩的日期，
//    public static Map<String, List<HistoryData>> getRecords(UUID uuid) {
//        HashMap<String, List<HistoryData>> result = new HashMap<>();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            List<Date> gameList = getDateList(uuid, FileType.GAME);
//            gameList.forEach(date -> {
//                String key = DATE_FORMAT.format(date);
//                //如未有List創建新的並將資料帶入
//                List<HistoryData> list = result.computeIfAbsent(key, k -> new ArrayList<>());
//                list.add(new HistoryData(FileType.GAME, date));
//            });
//            List<Date> shortLineList = getDateList(uuid, FileType.SHORT_LINE);
//            shortLineList.forEach(date -> {
//                String key = DATE_FORMAT.format(date);
//                List<HistoryData> list = result.computeIfAbsent(key, k -> new ArrayList<>());
//                list.add(new HistoryData(FileType.SHORT_LINE, date));
//            });
//        }
//        return result;
//    }
//
//    public static boolean deleteRecord(HistoryData data) {
//        String path = String.join("/", new String[]{
//                fileDir, Client.getUuid().toString(), data.type.displayName, Long.toString(data.date.getTime())
//        });
//        File folder = new File(path);
//        if (!folder.exists()) return false;
//        File[] files = folder.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                if (!file.delete()) return false;
//            }
//        }
//        return folder.delete();
//    }
//
//    //從資料中取得個人遊玩紀錄，根據遊戲類型取得路徑
//    private static List<Date> getDateList(UUID uuid, FileType type) {
//        String path = String.join("/", new String[]{
//                fileDir, uuid.toString(), type.displayName
//        });
//        //確認資料夾是否存在
//        checkFolder(path);
//        //listFiles可取得資料夾中所有檔案
//        File[] files = new File(path).listFiles();
//        if (files == null) return new ArrayList<>();
//        List<Date> fileList = new ArrayList<>(files.length);
//        for (File file : files) {
//            //parseLong 將字串轉換為Long
//            long time = Long.parseLong(file.getName());
//          //每個時間→日期(Date)、儲存每個時間成一個list(Array List)
//            fileList.add(new Date(time));
//        }
//        //回傳
//        return fileList;
//    }
//
//    private static void checkFolder(String folder) {
//        File dir = new File(folder);
//        if (dir.exists()) return;
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Files.createDirectories(Paths.get(folder));
//            }
//        } catch (IOException exp) {
//            exp.printStackTrace();
//        }
//    }
//
//}
