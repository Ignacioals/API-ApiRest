package nachoymica.com.ftp;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPConnect {

	private static FTPClient ftpclient;
	private static String ftpLocation = "C:\\Users\\ignac\\Desktop\\INTERACTIVAS\\FTP";
	private static String formatoImagen = ".png";
	
	public static FTPClient getConnection() throws SocketException {
		if (ftpclient == null) {
			ftpclient = new FTPClient();
			try {
				ftpclient.connect("127.0.0.1",64327);
				ftpclient.login("test", null);
				
				showServerReply(ftpclient);
				int replyCode = ftpclient.getReplyCode();
				//Si el server falla
				if(!FTPReply.isPositiveCompletion(replyCode)) {
					System.out.println("ERROR FTP: failed operation" + replyCode);
				} else {
		            ftpclient.enterLocalPassiveMode();
		            try {
						ftpclient.setFileType(FTP.BINARY_FILE_TYPE);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.out.println("Connected to server");
			} catch (IOException e) {
				System.out.println("Failed to connect to server");
			}
		}
		return ftpclient;
	}
	
	public static List<String> uploadFileList(List<File> images) {
		ArrayList<String> directions = new ArrayList<String>();
		
		if (images.isEmpty())
			return directions;
		
		if (ftpclient == null) {
			try {
				getConnection();		
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		
		String fileFtpName = null;
		InputStream inputStream = null;
		
		for(File file: images) {
			try {
				fileFtpName = ToHash.getFileHash(file);
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			try {
				inputStream = new FileInputStream(file);
				boolean uploaded = false;
				try {
					uploaded = ftpclient.storeFile(fileFtpName, inputStream);
				} catch (IOException e) {
					System.out.println("Error saving file");
					e.printStackTrace();
				}
				if(uploaded) {
					System.out.println("File uploaded succesfully");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {	
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			String fileLocation = new String(ftpLocation + "\\" + fileFtpName);
			directions.add(fileLocation);
		}
		
		return directions;
	}
	
	public static String uploadFile(File file) {
		
		if (ftpclient == null) {
			try {
				getConnection();		
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
		
		String fileFtpName = null;
		InputStream inputStream = null;
		
		try {
				fileFtpName = ToHash.getFileHash(file);
			} catch (NoSuchAlgorithmException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			try {
				inputStream = new FileInputStream(file);
				boolean uploaded = false;
				try {
					uploaded = ftpclient.storeFile(fileFtpName + formatoImagen, inputStream);
				} catch (IOException e) {
					System.out.println("Error saving file.");
					e.printStackTrace();
				}
				if(uploaded) {
					System.out.println("File uploaded succsefully");
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {	
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		String fileLocation= new String(ftpLocation + "\\" + fileFtpName + formatoImagen);
		System.out.println(fileLocation);
		return fileLocation;
	}
	
    private static void showServerReply(FTPClient ftpClient) {
        String[] replies = ftpClient.getReplyStrings();
        if (replies != null && replies.length > 0) {
            for (String aReply : replies) {
                System.out.println("SERVER: " + aReply);
            }
        }
    }
	
	
}