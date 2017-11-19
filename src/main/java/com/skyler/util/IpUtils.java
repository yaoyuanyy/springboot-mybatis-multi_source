/**   
* @Title: IpUtils.java 
* @Package com.bafang.metal.common.utils 
* @Description:  
* @author liuqing   
* @date 2017年2月4日 下午3:50:00 
* @version V1.0   
*/
package com.skyler.util;

/** 
 * @ClassName: IpUtils 
 * @Description: 
 * @author liuqing 
 * @date 2017年2月4日 下午3:50:00 
 *  
 */
public class IpUtils {

	public static long ipToLong(String strIP){
		try{
			if(strIP == null || strIP.length() == 0){
				return 0L;
			}
			long[] ip = new long[4];  
			int position1 = strIP.indexOf(".");  
			int position2 = strIP.indexOf(".",position1+1);  
			int position3 = strIP.indexOf(".",position2+1);      
			ip[0] = Long.parseLong(strIP.substring(0,position1));  
			ip[1] = Long.parseLong(strIP.substring(position1+1,position2));  
			ip[2] = Long.parseLong(strIP.substring(position2+1,position3));  
			ip[3] = Long.parseLong(strIP.substring(position3+1));  
			return (ip[0]<<24)+(ip[1]<<16)+(ip[2]<<8)+ip[3]; 
		}catch(Exception ex){
			return 0L;
		}
	} 
	public static String longToip(long ipLong) {   
        long mask[] = {0x000000FF,0x0000FF00,0x00FF0000,0xFF000000};   
        long num = 0;   
        StringBuffer ipInfo = new StringBuffer();   
        for(int i=0;i<4;i++){   
            num = (ipLong & mask[i])>>(i*8);   
            if(i>0) ipInfo.insert(0,".");   
            ipInfo.insert(0,Long.toString(num,10));   
        }   
        return ipInfo.toString();   
    }
	
}
