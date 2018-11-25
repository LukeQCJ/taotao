package com.taotao.fastdfs;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import com.taotao.utils.FastDFSClient;

public class TestFastDFS {

	@Test
	public void uploadFile() throws Exception {
		//向工程中添加jar包
		//创建一个配置文件，配置tracker服务器地址
		//加载配置文件
		ClientGlobal.init("D:/javaLearning/workSpaceTaoTao/taotao-manager-web/src/main/resources/resource/client.conf");
		//创建一个TrackerClient对象
		TrackerClient trackerClient = new TrackerClient();
		//使用TrackerClient对象获得trackerserver对象
		TrackerServer trackerServer = trackerClient.getConnection();
		//创建一个StorageServer的引用null就可以
		StorageServer storageServer = null;
		//创建一个StorageClient对象，trackerserver、storageServer两个参数
		StorageClient storageClient = new StorageClient(trackerServer,storageServer);
		//使用StorageClient对象上传文件
		String[] strings = storageClient.upload_file("C:\\Users\\Luke\\Desktop\\beautiful.jpg", "jpg", null);
		for(String s:strings){
			System.out.println(s);
		}
	}
	
	@Test
	public void testFastDFSClient() throws Exception{
		FastDFSClient dfs = new FastDFSClient("D:\\javaLearning\\workSpaceTaoTao\\taotao-manager-web\\src\\main\\resources\\resource\\client.conf");
		String fileName = "C:\\Users\\Luke\\Desktop\\beautiful.jpg";
		String str = dfs.uploadFile(fileName);
		System.out.println(str);
	}
}
