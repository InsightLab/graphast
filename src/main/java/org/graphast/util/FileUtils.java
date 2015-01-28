package org.graphast.util;

import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.longs.Long2ShortMap;
import it.unimi.dsi.fastutil.longs.Long2ShortOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;
import it.unimi.dsi.fastutil.shorts.ShortBigArrayBigList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtils {

	public static String read(String file) throws IOException{
		InputStream is = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = br.readLine()) != null){
			sb.append(line).append("\n");
		}
		
		br.close();
		return sb.toString();
	}
	
	public static String getResourcePath(String resource){
		return FileUtils.class.getResource(resource).getPath();
	}
	
	public static InputStream getResourceStream(String resource){
		return FileUtils.class.getResourceAsStream(resource);
	}
	
	public static void saveIntList(String path, IntBigArrayBigList list, int blockSize) throws IOException{
		String dir = path.substring(0, path.lastIndexOf("/"));
		createDir(dir);
		FileOutputStream fos = new FileOutputStream(path);
		FileChannel channel = fos.getChannel();
		
		ByteBuffer buf = ByteBuffer.allocate(4 * blockSize);
		int capacity = buf.capacity();
		int used = 0;
		for(long l = 0; l < list.size64(); l++){
			buf.putInt(list.getInt(l));
			used += 4;
			if(used == capacity){
				buf.flip();
				channel.write(buf);
				buf = ByteBuffer.allocate(4 * blockSize);
				used = 0;
			}else if(l == list.size64() - 1 && used < capacity){
				buf.flip();
				channel.write(buf);
			}
		}
		channel.close();
		fos.close();
	}
	
	public static void saveShortList(String path, ShortBigArrayBigList list, int blockSize) throws IOException{
		String dir = path.substring(0, path.lastIndexOf("/"));
		createDir(dir);
		FileOutputStream fos = new FileOutputStream(path);
		FileChannel channel = fos.getChannel();
		
		ByteBuffer buf = ByteBuffer.allocate(2 * blockSize);
		int capacity = buf.capacity();
		int used = 0;
		for(long l = 0; l < list.size64(); l++){
			buf.putShort(list.getShort(l));
			used += 2;
			if(used == capacity){
				buf.flip();
				channel.write(buf);
				buf = ByteBuffer.allocate(2 * blockSize);
				used = 0;
			}else if(l == list.size64() - 1 && used < capacity){
				buf.flip();
				channel.write(buf);
			}
		}
		channel.close();
		fos.close();
	}
	
	public static void saveLong2ShortMap(String path, Long2ShortMap map, int blockSize) throws IOException{
		String dir = path.substring(0, path.lastIndexOf("/"));
		createDir(dir);
		FileOutputStream fos = new FileOutputStream(path);
		FileChannel channel = fos.getChannel();
		
		ByteBuffer buf = ByteBuffer.allocate(10 * blockSize);
		int capacity = buf.capacity();
		int used = 0;
		LongIterator iterator = map.keySet().iterator();
		
		while(iterator.hasNext()) {
			long key = iterator.next();
			buf.putLong(key);
			buf.putShort(map.get(key));
			used += 10;
			if(used == capacity) {
				buf.flip();
				channel.write(buf);
				buf = ByteBuffer.allocate(10 * blockSize);
				used = 0;
			} else if(!iterator.hasNext() && used < capacity){
				buf.flip();
				channel.write(buf);
			}
		}
		
		channel.close();
		fos.close();
	}
	
	public static void saveStringList(String path, ObjectBigList<String> list, int blockSize) throws IOException{
		String dir = path.substring(0, path.lastIndexOf("/"));
		createDir(dir);
		FileOutputStream fos = new FileOutputStream(path);
		FileChannel channel = fos.getChannel();
		
		ByteBuffer buf = ByteBuffer.allocate(4 * blockSize);
		int capacity = buf.capacity();
		int used = 0;
		for(long l = 0; l < list.size64(); l++){
			String s = list.get(l);
			if(s==null){
				s="\f";
			}
			for(char c : s.toCharArray()){
				buf.putChar(c);
				used += 4;
				if(used == capacity){
					buf.flip();
					channel.write(buf);
					buf = ByteBuffer.allocate(4 * blockSize);
					used = 0;
				}
			}
			
			if(!s.equals("\f")) { 
				buf.putChar('\n');
			}
			used += 4;
			if(used == capacity){
				buf.flip();
				channel.write(buf);
				buf = ByteBuffer.allocate(4 * blockSize);
				used = 0;
			}else if(l == list.size64() - 1 && used < capacity){
				buf.flip();
				channel.write(buf);
			}
		}
		channel.close();
		fos.close();
	}
	
	public static IntBigArrayBigList loadIntList(String path, int blockSize) throws IOException{
		IntBigArrayBigList list = new IntBigArrayBigList();
		FileInputStream fis = new FileInputStream(path);
		FileChannel channel = fis.getChannel();
		
		ByteBuffer buf = ByteBuffer.allocate(4 * blockSize);
		while (channel.read(buf) > 0) {
			buf.flip();
            while (buf.hasRemaining()) {
                list.add(buf.getInt());
            }
            buf.clear();
		}
		channel.close();
		fis.close();
		return list;
	}
	
	public static ShortBigArrayBigList loadShortList(String path, int blockSize) throws IOException{
		 
		ShortBigArrayBigList list = new ShortBigArrayBigList();
		FileInputStream fis = new FileInputStream(path);
		FileChannel channel = fis.getChannel();
		
		ByteBuffer buf = ByteBuffer.allocate(4 * blockSize);
		while (channel.read(buf) > 0) {
			buf.flip();
            while (buf.hasRemaining()) {
                list.add(buf.getShort());
            }
            buf.clear();
		}
		channel.close();
		fis.close();
		return list;
	}
	
	public static Long2ShortMap loadLong2ShortMap(String path, int blockSize) throws IOException{
		 
		Long2ShortMap list = new Long2ShortOpenHashMap();
		FileInputStream fis = new FileInputStream(path);
		FileChannel channel = fis.getChannel();
		
		ByteBuffer buf = ByteBuffer.allocate(10 * blockSize);
		while (channel.read(buf) > 0) {
			buf.flip();
            while (buf.hasRemaining()) {
            	list.put(buf.getLong(), buf.getShort());
            }
            buf.clear();
		}
		channel.close();
		fis.close();
		return list;
	}
	
	public static ObjectBigList<String> loadStringList(String path, int blockSize) throws IOException{
		ObjectBigList<String> list = new ObjectBigArrayBigList<String>();
		FileInputStream fis = new FileInputStream(path);
		FileChannel channel = fis.getChannel();
		
		ByteBuffer buf = ByteBuffer.allocate(4 * blockSize);
		while (channel.read(buf) > 0) {
			buf.flip();
			String s = "";
            while (buf.hasRemaining()) {
            	char c = buf.getChar();
            	if(c!='\n'){
            		if(c=='\f'){
            			list.add(null);
            		} else{
            			s+=c;
            		}
            	} else{
            		list.add(s);
            		s="";
            	}
            }
            buf.clear();
		}
		channel.close();
		fis.close();
		return list;
	}
	
	
	
	public static void createDir(String dir){
		File pathName = new File(dir);
		if(!pathName.isDirectory()){
			pathName.mkdirs();
		}
	}
	
	public static void deleteDir(String dir) {
		File pathName = new File(dir);
		if(!pathName.isDirectory()) {
			pathName.delete();
		}
	}
	
}
