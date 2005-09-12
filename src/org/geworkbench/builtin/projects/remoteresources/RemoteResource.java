package org.geworkbench.builtin.projects.remoteresources;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
/**
 *A simple wrapper class for resources
 */
public class RemoteResource {
    private String username;
    private String password;
    private String connectProtocol;
    private String DEFAULTPROTOCAL = "http";
    private String shortname;
    private String uri;
    public RemoteResource() {
    }
    public RemoteResource(String url, String protocal, String user, String passwd){
        this("Default",  url, protocal, user,  passwd);
    }
    public RemoteResource(String shortname, String url, String protocal, String user, String passwd){
//       try{
//           uri = url;
//       }catch(MalformedURLException e){e.printStackTrace();}
       uri = url;
       connectProtocol = protocal;
       username = user;
       password = passwd;
       this.shortname = shortname;
   }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConnectProtocal(String connectProtocol) {
        this.connectProtocol = connectProtocol;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConnectProtocal() {
        return connectProtocol;
    }

    public String getShortname() {
        return shortname;
    }

    public String getUri() {
        return uri;
    }

    /**
     * update
     *
     * @param rResource RemoteResource
     */
    public void update(RemoteResource rResource) {
        shortname = rResource.shortname;
        uri = rResource.uri;
        username = rResource.username;
        password = rResource.password;
        connectProtocol = rResource.connectProtocol;
    }


}
