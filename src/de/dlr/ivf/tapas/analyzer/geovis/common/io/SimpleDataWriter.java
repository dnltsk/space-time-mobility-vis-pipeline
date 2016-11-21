package de.dlr.ivf.tapas.analyzer.geovis.common.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONAware;

public class SimpleDataWriter {

	protected List<String> targetPaths = null;
	
	public SimpleDataWriter(String targetPath){
		this(Arrays.asList(targetPath));
	}
	
	public SimpleDataWriter(List<String> targetPaths){
		this.targetPaths = targetPaths;
	}
	
	public void clearTargetPath() {
		for(String targetPath : targetPaths){
			File targetDir = new File(targetPath);
			if(targetDir.exists()){
				try {
					FileUtils.cleanDirectory(targetDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void writeJsonFile(String filename, JSONAware json){
		for(String targetPath : targetPaths){
			File targetFile = new File(targetPath+filename);
			//System.out.println("try writing file = "+targetFile.getAbsolutePath());
			BufferedWriter out = null;
			try {
				File folder = targetFile.getParentFile();
				if(!folder.exists()){
					folder.mkdirs();
				}
				out = new BufferedWriter(new PrintWriter(targetFile, "UTF-8"),1024*1024);
				out.write(json.toJSONString());
				//out.flush();
			} catch (IOException e) {
				System.err.println("Fehler beim Schreiben von Datei "+targetFile.getAbsolutePath()+" mit Inhalt "+json.toJSONString());
				e.printStackTrace();
			} finally{
				if(out != null){
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}
