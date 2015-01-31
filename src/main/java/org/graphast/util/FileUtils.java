package org.graphast.util;

import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
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
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.graphast.enums.CompressionType;

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

	public static Channel getOutputChannel(String path, CompressionType compressionType) throws IOException {
		FileOutputStream fos = new FileOutputStream(path);
		Channel channel = null;
		if (compressionType == CompressionType.NO_COMPRESSION) {
			channel = fos.getChannel();
		} else  if (compressionType == CompressionType.GZIP_COMPRESSION) {
			GZIPOutputStream gos = new GZIPOutputStream(fos);
			channel = Channels.newChannel(gos);
		}
		return channel;
	}

	public static Channel getInputChannel(String path, CompressionType compressionType) throws IOException {
		FileInputStream fos = new FileInputStream(path);
		Channel channel = null;
		if (compressionType == CompressionType.NO_COMPRESSION) {
			channel = fos.getChannel();
		} else  if (compressionType == CompressionType.GZIP_COMPRESSION) {
			GZIPInputStream gos = new GZIPInputStream(fos);
			channel = Channels.newChannel(gos);
		}
		return channel;
	}

	public static void write(Channel channel, ByteBuffer buf) throws IOException {
		if (channel instanceof WritableByteChannel) {
			((WritableByteChannel)channel).write(buf);
		} else if (channel instanceof FileChannel) {
			((FileChannel)channel).write(buf);
		} else {
			throw new IOException("Invalid channel: " + channel);
		}
	}

	public static int read(Channel channel, ByteBuffer buf) throws IOException {
		int result;
		if (channel instanceof ReadableByteChannel) {
			result = ((ReadableByteChannel)channel).read(buf);
		} else if (channel instanceof FileChannel) {
			result = ((FileChannel)channel).read(buf);
		} else {
			throw new IOException("Invalid channel: " + channel);
		}
		return result;
	}

	public static void saveIntList(String path, IntBigArrayBigList list, int blockSize, CompressionType compressionType) throws IOException{
		String dir = path.substring(0, path.lastIndexOf("/"));
		createDir(dir);
		Channel channel = getOutputChannel(path, compressionType);

		ByteBuffer buf = ByteBuffer.allocate(4 * blockSize);
		int capacity = buf.capacity();
		int used = 0;
		for(long l = 0; l < list.size64(); l++){
			buf.putInt(list.getInt(l));
			used += 4;
			if(used == capacity){
				buf.flip();
				write(channel, buf); 
				buf = ByteBuffer.allocate(4 * blockSize);
				used = 0;
			}else if(l == list.size64() - 1 && used < capacity){
				buf.flip();
				write(channel, buf); 
			}
		}
		channel.close();
	}

	public static void saveShortList(String path, ShortBigArrayBigList list, int blockSize, CompressionType compressionType) throws IOException{
		String dir = path.substring(0, path.lastIndexOf("/"));
		createDir(dir);
		Channel channel = getOutputChannel(path, compressionType);

		ByteBuffer buf = ByteBuffer.allocate(2 * blockSize);
		int capacity = buf.capacity();
		int used = 0;
		for(long l = 0; l < list.size64(); l++){
			buf.putShort(list.getShort(l));
			used += 2;
			if(used == capacity){
				buf.flip();
				write(channel, buf);
				buf = ByteBuffer.allocate(2 * blockSize);
				used = 0;
			}else if(l == list.size64() - 1 && used < capacity){
				buf.flip();
				write(channel, buf);
			}
		}
		channel.close();
	}

	public static void saveLong2IntMap(String path, Long2IntMap map, int blockSize, CompressionType compressionType) throws IOException{
		String dir = path.substring(0, path.lastIndexOf("/"));
		createDir(dir);
		Channel channel = getOutputChannel(path, compressionType);

		ByteBuffer buf = ByteBuffer.allocate(10 * blockSize);
		int capacity = buf.capacity();
		int used = 0;
		LongIterator iterator = map.keySet().iterator();

		while(iterator.hasNext()) {
			long key = iterator.next();
			buf.putLong(key);
			buf.putInt(map.get(key));
			used += 10;
			if(used == capacity) {
				buf.flip();
				write(channel, buf);
				buf = ByteBuffer.allocate(10 * blockSize);
				used = 0;
			} else if(!iterator.hasNext() && used < capacity){
				buf.flip();
				write(channel, buf);
			}
		}

		channel.close();
	}

	public static void saveStringList(String path, ObjectBigList<String> list, int blockSize, CompressionType compressionType) throws IOException{
		String dir = path.substring(0, path.lastIndexOf("/"));
		createDir(dir);
		Channel channel = getOutputChannel(path, compressionType);

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
					write(channel, buf);
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
				write(channel, buf);
				buf = ByteBuffer.allocate(4 * blockSize);
				used = 0;
			}else if(l == list.size64() - 1 && used < capacity){
				buf.flip();
				write(channel, buf);
			}
		}
		channel.close();
	}

	public static IntBigArrayBigList loadIntList(String path, int blockSize, CompressionType compressionType) throws IOException{
		IntBigArrayBigList list = new IntBigArrayBigList();
		Channel channel = getInputChannel(path, compressionType);

		ByteBuffer buf = ByteBuffer.allocate(4 * blockSize);
		while (read(channel, buf) > 0) {
			buf.flip();
			while (buf.hasRemaining()) {
				list.add(buf.getInt());
			}
			buf.clear();
		}
		channel.close();
		return list;
	}

	public static ShortBigArrayBigList loadShortList(String path, int blockSize, CompressionType compressionType) throws IOException{

		ShortBigArrayBigList list = new ShortBigArrayBigList();
		Channel channel = getInputChannel(path, compressionType);

		ByteBuffer buf = ByteBuffer.allocate(4 * blockSize);
		while (read(channel, buf) > 0) {
			buf.flip();
			while (buf.hasRemaining()) {
				list.add(buf.getShort());
			}
			buf.clear();
		}
		channel.close();
		return list;
	}

	public static Long2IntMap loadLong2IntMap(String path, int blockSize, CompressionType compressionType) throws IOException{

		Long2IntMap list = new Long2IntOpenHashMap();
		Channel channel = getInputChannel(path, compressionType);

		ByteBuffer buf = ByteBuffer.allocate(10 * blockSize);
		while (read(channel, buf) > 0) {
			buf.flip();
			while (buf.hasRemaining()) {
				list.put(buf.getLong(), buf.getInt());
			}
			buf.clear();
		}
		channel.close();
		return list;
	}

	public static ObjectBigList<String> loadStringList(String path, int blockSize, CompressionType compressionType) throws IOException{
		ObjectBigList<String> list = new ObjectBigArrayBigList<String>();
		Channel channel = getInputChannel(path, compressionType);

		ByteBuffer buf = ByteBuffer.allocate(4 * blockSize);
		while (read(channel, buf) > 0) {
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