package com.perm.kate.api.sample;

import java.io.File;

import com.rappasocial.vk15puzzle.ExtendedApplication;

import android.content.Context;
import android.widget.Toast;

public class FileCache {
    
    private File cacheDir;
    private Context context;
    
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"VK15PuzzleCache");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
        this.context = context;
    }
    
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
    	String filename = "";
    	try {
    		filename=String.valueOf(url.hashCode());
    	}
    	catch (Exception e){
    		org.holoeverywhere.widget.Toast.makeText(this.context, url + " ", org.holoeverywhere.widget.Toast.LENGTH_SHORT).show();
    	}
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;
        
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}