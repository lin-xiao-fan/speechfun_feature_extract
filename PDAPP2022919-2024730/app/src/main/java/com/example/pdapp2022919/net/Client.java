package com.example.pdapp2022919.net;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.pdapp2022919.Database.User.User;
import com.example.pdapp2022919.Database.User.UserDao;
import com.example.pdapp2022919.SystemManager.DatabaseManager;
import com.dtx804lab.dtx_netty_lib.base.RawMessageCodec;
import com.dtx804lab.dtx_netty_lib.message.FileMessage;
import com.dtx804lab.dtx_netty_lib.message.KeyMessage;
import com.dtx804lab.dtx_netty_lib.message.LoginMessagePD;
import com.dtx804lab.dtx_netty_lib.message.RegisterMessagePD;
import com.dtx804lab.dtx_netty_lib.message.UUIDMessage;
import com.dtx804lab.dtx_netty_lib.secure.AesCodec;
import com.dtx804lab.dtx_netty_lib.secure.RsaCodec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.github.cdimascio.dotenv.Dotenv;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class Client {

    public static final UUID GUEST_UUID = new UUID(0, 0);

    private static final int bufferSize = 1024 * 32;
    private static final String host = "140.135.101.62";
    private static final RSAPublicKey publicKey;
    private static final SecretKeySpec aesKeySpec;
    private static final IvParameterSpec ivSpec;
    private static final Cipher cipher;
    private static final KeyGenerator keyGen;

    private static UUID uuid = GUEST_UUID;

    static {
        Dotenv dotenv = Dotenv.configure()
                .directory("/assets")
                .filename("env")
                .load();
        RSAPublicKey pubKey = null;
        SecretKeySpec aesKey = null;
        IvParameterSpec ivPar = null;
        Cipher ci = null;
        KeyGenerator gen = null;
        byte[] pub = decodeHex(Objects.requireNonNull(dotenv.get("PUBLIC_KEY")));
        byte[] aes = decodeHex(Objects.requireNonNull(dotenv.get("SECRET_KEY")));
        byte[] iv = decodeHex(Objects.requireNonNull(dotenv.get("IV_PARAMETER")));
        try {
            pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pub));
            aesKey = new SecretKeySpec(aes, "AES");
            ivPar = new IvParameterSpec(iv);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            ci = Cipher.getInstance("AES/CTR/NoPadding");
            gen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        publicKey = pubKey;
        aesKeySpec = aesKey;
        ivSpec = ivPar;
        cipher = ci;
        keyGen = gen;
        keyGen.init(KeyMessage.KEY_LENGTH);
    }

    private static byte[] decodeHex(String s) {
        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (Character.digit(s.charAt(i * 2), 16) << 4);
            data[i] |= Character.digit(s.charAt(i * 2 + 1), 16);
        }
        return data;
    }

    public static void loadUuid(Context context) {
        UserDao dao = DatabaseManager.getInstance(context).userDao();
        User user = dao.getUser();
        if (user == null) {
            user = new User(
                    Client.GUEST_UUID.toString(),
                    Integer.toString(123456789),
                    "Guest",
                    0,
                    2
            );
            dao.addUser(user);
        }
        uuid = UUID.fromString(user.uuid);
    }

    public static UUID getUuid() {
        return uuid;
    }

    public static void logout() {
        uuid = GUEST_UUID;
    }

    public static void login(Context context, int id, String name, ClientCallback callback) {
        setupClient(context, new ChannelInboundHandlerAdapter() {

            @Override
            public void channelActive(@NonNull ChannelHandlerContext ctx) {
                LoginMessagePD msg = new LoginMessagePD(id, name);
                ctx.writeAndFlush(LoginMessagePD.Companion.encoder(ctx, msg));
            }

            @Override
            public void channelRead(@NonNull ChannelHandlerContext ctx, @NonNull Object msg) {
                if (msg instanceof UUIDMessage) {
                    Client.uuid = ((UUIDMessage) msg).getUuid();
                    callback.callback(!uuid.equals(GUEST_UUID));
                }
            }
        });
    }

    public static void register(Context context, int id, String name, ClientCallback callback) {
        setupClient(context, new ChannelInboundHandlerAdapter() {

            @Override
            public void channelActive(@NonNull ChannelHandlerContext ctx) {
                RegisterMessagePD msg = new RegisterMessagePD(id, name);
                ctx.writeAndFlush(RegisterMessagePD.Companion.encoder(ctx, msg));
            }

            @Override
            public void channelRead(@NonNull ChannelHandlerContext ctx, @NonNull Object msg) {
                if (msg instanceof UUIDMessage) {
                    Client.uuid = ((UUIDMessage) msg).getUuid();
                    callback.callback(!uuid.equals(GUEST_UUID));
                }
            }

        });
    }

    public static void upload(Context context, File[] files, ClientCallback callback) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
        ByteBuffer encryptBuffer = ByteBuffer.allocateDirect(bufferSize);
        setupClient(context, new ChannelInboundHandlerAdapter() {

            @Override
            public void channelActive(@NonNull ChannelHandlerContext ctx) {
                ctx.writeAndFlush(UUIDMessage.Companion.encoder(ctx, new UUIDMessage(uuid)));
                File uploadFile = files.length > 1 ? zipFile(files) : files[0];
                //製作加密鑰匙
                SecretKey key = keyGen.generateKey();
                try {
                    cipher.init(Cipher.ENCRYPT_MODE, key, Client.ivSpec);
                    ctx.writeAndFlush(KeyMessage.Companion.encoder(ctx, new KeyMessage(key)));
                } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
                    e.printStackTrace();
                }
                try {
                    //讀取檔案將檔案分段
                    FileChannel channel = new RandomAccessFile(uploadFile, "r").getChannel();
                    boolean isRemaining = true;
                    while (isRemaining) {
                        isRemaining = channel.read(buffer) == bufferSize;
                        buffer.flip();

                        // encrypt 將檔案加密
                        if (isRemaining) cipher.update(buffer, encryptBuffer);
                        else cipher.doFinal(buffer, encryptBuffer);
                        encryptBuffer.flip();

                        ChannelFuture future = ctx.writeAndFlush(FileMessage.Companion.encoder(
                                ctx, new FileMessage(uploadFile.getName(), isRemaining, encryptBuffer)
                        ));
                        //確認全部已上傳
                        if (!isRemaining) {
                            future.addListener((ChannelFutureListener) listener -> {
                                ctx.close();
                            });
                            callback.callback(true);
                        }

                        buffer.clear();
                        encryptBuffer.clear();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.callback(false);
                }
            }
        });
    }

    private static void setupClient(Context context, ChannelInboundHandlerAdapter handler) {
        // TODO 網路確認
//        new Handler(Looper.getMainLooper()).post(() -> {
//            Toast.makeText(context, "No internet connect", Toast.LENGTH_SHORT).show();
//        });
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ChannelFuture future = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {

                        @Override
                        protected void initChannel(@NonNull NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(10240, 0, 2, 0, 2));
                            ch.pipeline().addLast(new LengthFieldPrepender(2));
                            ch.pipeline().addLast(new AesCodec(aesKeySpec, ivSpec));
                            ch.pipeline().addLast(new RawMessageCodec());
                            ch.pipeline().addLast(new RsaCodec(publicKey, null));
                            ch.pipeline().addLast(handler);
                        }

                    })
                    .connect(new InetSocketAddress(host, 8787))
                    .addListener(future1 -> {
                        if (future1.isSuccess()) {
                            System.out.println("suc");
                        } else System.out.println("fail");
                    });
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private static File zipFile(File[] files) {
        File outputFile = new File(getZipPath(files[0]));
        if (outputFile.exists()) return outputFile;
        try (
                FileOutputStream fos = new FileOutputStream(outputFile); // 輸出檔名
                ZipOutputStream zipOut = new ZipOutputStream(fos) // 用檔案輸出流建立出 Zip 輸出流
        ) {
            for (File file : files) {
                // 在 zip 內加入一個項目 (可以是一個檔名，或用目錄來表示)
                zipOut.putNextEntry(new ZipEntry(file.getName()));
                try (FileInputStream fis = new FileInputStream(file)) {
                    while (fis.available() > 0) {
                        byte[] buffer = new byte[Math.min(fis.available(), bufferSize)];
                        fis.read(buffer);
                        zipOut.write(buffer);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }

    private static String getZipPath(File file) {
        String[] split = file.getName().split("_");
        return file.getParent() + "/" + split[0] + "_" + split[1] + "_all.zip";
    }

}
