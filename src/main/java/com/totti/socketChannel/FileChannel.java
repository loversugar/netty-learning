package com.totti.socketChannel;

import org.omg.IOP.ExceptionDetailMessage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

public class FileChannel {
    public static void main(String[] args) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("/code/11.txt", "rw");
            java.nio.channels.FileChannel fileChannel = randomAccessFile.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(4);
            int byteReads = fileChannel.read(byteBuffer);
            while (byteReads != -1) {
                System.out.println("begin....");
                System.out.println(new String(byteBuffer.array()));
                System.out.println("end....");

                byteBuffer.clear();
                byteReads = fileChannel.read(byteBuffer);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
