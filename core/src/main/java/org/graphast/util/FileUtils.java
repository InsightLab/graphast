package org.graphast.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.graphast.config.Configuration;
import org.graphast.enums.CompressionType;
import org.graphast.exception.GraphastException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBigArrayBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigList;
import it.unimi.dsi.fastutil.shorts.ShortBigArrayBigList;

public class FileUtils {

	private static Logger log = LoggerFactory.getLogger(FileUtils.class); 
	
	public static String read(String file) {
		try {
			InputStream is = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String line;
			while((line = br.readLine()) != null){
				sb.append(line).append("\n");
			}

			br.close();
			return sb.toString();
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static String getResourcePath(String resource){
		return FileUtils.class.getResource(resource).getPath();
	}

	public static InputStream getResourceStream(String resource){
		return FileUtils.class.getResourceAsStream(resource);
	}

	public static Channel getOutputChannel(String path, CompressionType compressionType) {
		try {
			FileOutputStream fos = new FileOutputStream(path);
			Channel channel = null;
			if (compressionType == CompressionType.NO_COMPRESSION) {
				channel = fos.getChannel();
			} else  if (compressionType == CompressionType.GZIP_COMPRESSION) {
				GZIPOutputStream gos = new GZIPOutputStream(fos);
				channel = Channels.newChannel(gos);
			}
			return channel;
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static Channel getInputChannel(String path, CompressionType compressionType) {
		try {
			FileInputStream fos = new FileInputStream(path);
			Channel channel = null;
			if (compressionType == CompressionType.NO_COMPRESSION) {
				channel = fos.getChannel();
			} else  if (compressionType == CompressionType.GZIP_COMPRESSION) {
				GZIPInputStream gos = new GZIPInputStream(fos);
				channel = Channels.newChannel(gos);
			}
			return channel;
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static void write(Channel channel, ByteBuffer buf) {
		try {
			if (channel instanceof WritableByteChannel) {
				((WritableByteChannel)channel).write(buf);
			} else if (channel instanceof FileChannel) {
				((FileChannel)channel).write(buf);
			} else {
				throw new GraphastException("Invalid channel: " + channel);
			}
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static int read(Channel channel, ByteBuffer buf) {
		try {
			int result;
			if (channel instanceof ReadableByteChannel) {
				result = ((ReadableByteChannel)channel).read(buf);
			} else if (channel instanceof FileChannel) {
				result = ((FileChannel)channel).read(buf);
			} else {
				throw new GraphastException("Invalid channel: " + channel);
			}
			return result;
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static void saveIntList(String path, IntBigArrayBigList list, int blockSize, CompressionType compressionType) {
		try {
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
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static void saveShortList(String path, ShortBigArrayBigList list, int blockSize, CompressionType compressionType) {
		try {
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
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static void saveLong2IntMap(String path, Long2IntMap map, int blockSize, CompressionType compressionType) {
		try {
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
			
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static void saveStringList(String path, ObjectBigList<String> list, int blockSize, CompressionType compressionType) {
		try {
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
			
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static IntBigArrayBigList loadIntList(String path, int blockSize, CompressionType compressionType) {
		try {
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
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static ShortBigArrayBigList loadShortList(String path, int blockSize, CompressionType compressionType) {
		try {
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
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static Long2IntMap loadLong2IntMap(String path, int blockSize, CompressionType compressionType) {
		try {
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
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}

	public static ObjectBigList<String> loadStringList(String path, int blockSize, CompressionType compressionType) {
		try {
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
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		}
	}



	public static void createDir(String dir){
		File pathName = new File(dir);
		if(!pathName.isDirectory()){
			pathName.mkdirs();
		}
	}

	public static void deleteDir(String dir) {
		if (dir == null) {
			return;
		}
		File pathName = new File(dir);
		deleteDir(pathName);
	}
	
	public static void deleteDir(File dir) {
		
		if(dir.isDirectory()) {
			cleanDirectory(dir);
		}
        if (!dir.delete() && dir.exists()) {
            String message =
                "Unable to delete directory " + dir + ".";
            throw new GraphastException(message);
        }
	}
	
    /**
     * Clean a directory without deleting it.
     * @param directory directory to clean
     */
    private static void cleanDirectory(File directory) {
        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new GraphastException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new GraphastException(message);
        }

        File[] files = directory.listFiles();
        if (files == null) {  // null if security restricted
            throw new GraphastException("Failed to list contents of " + directory);
        }

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            forceDelete(file);
        }
    }
    
    /**
     * <p>
     * Delete a file. If file is a directory, delete it and all sub-directories.
     * </p>
     * <p>
     * The difference between File.delete() and this method are:
     * </p>
     * <ul>
     * <li>A directory to be deleted does not have to be empty.</li>
     * <li>You get exceptions when a file or directory cannot be deleted.
     *      (java.io.File methods returns a boolean)</li>
     * </ul>
     * @param file file or directory to delete.
     */
    private static void forceDelete(File file) {
        if (file.isDirectory()) {
            deleteDir(file);
        } else {
            if (!file.exists()) {
                throw new GraphastException("File does not exist: " + file);
            }
            if (!file.delete()) {
                String message =
                    "Unable to delete file: " + file;
                throw new GraphastException(message);
            }
        }
    }
    
    public static String download(String url, String path) {
	    	if (url == null) {
	    		throw new GraphastException("URL can not be null.");
	    	}
	    	String result = null;
	    	FileOutputStream fos = null;
		try {
			log.info("Downloading from {}", url);
			URL website = new URL(url);
		    	ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		    	String file = website.getPath();
		    	file = file.substring(file.lastIndexOf('/') + 1);
		    	result = path + "/" + file;
		    	fos = new FileOutputStream(result);
		    	fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		    	log.info("Successful download of file {}", result);
		} catch (IOException e) {
			throw new GraphastException(e.getMessage(), e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					throw new GraphastException(e.getMessage(), e);
				}
			}
		}
		return result;
    }

    public static long folderSize(String directory) {
    		if (directory == null) {
    			return 0;
    		}
    		return folderSize(new File(directory));
    }
    
    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }
    
    public static String getAbsolutePath(String path) {
		return path.replace("${user.home}", Configuration.USER_HOME);
    }
    
}
