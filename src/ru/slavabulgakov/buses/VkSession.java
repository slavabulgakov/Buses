package ru.slavabulgakov.buses;

import android.content.Context;
import android.content.SharedPreferences;

public class VkSession {
    private SharedPreferences _prefs;
    private final String PREFS_NAME = "Vk:Settings";
    private Context _context;
    private SharedPreferences.Editor _editor;
    
    public VkSession(){}
    
    public VkSession(Context context){
    	_context = context;
    	_prefs = _context.getSharedPreferences(PREFS_NAME, 0);
        _editor = _prefs.edit();
    }
    
    public void saveAccessToken(String accessToken, String expires, String userId){
    	_editor.putString("VkAccessToken", accessToken);
    	_editor.putString("VkExpiresIn", expires);
    	_editor.putString("VkUserId", userId);
    	_editor.putLong("VkAccessTime", System.currentTimeMillis());
    	_editor.commit();
    }
    
    public String[] getAccessToken(){
    	String[] params = new String[4];
    	params[0] = _prefs.getString("VkAccessToken", "");
    	params[1] = _prefs.getString("VkExpiresIn", "");
    	params[2] = _prefs.getString("VkUserId", "");
    	params[3] =  String.valueOf(_prefs.getLong("VkAccessTime",0));
    	return params;
    }
    
    public void resetAccessToken(){
    	_editor.putString("VkAccessToken", "");
    	_editor.putString("VkExpiresIn", "");
    	_editor.putString("VkUserId", "");
    	_editor.putLong("VkAccessTime", 0);
    	_editor.commit();
    }   
}