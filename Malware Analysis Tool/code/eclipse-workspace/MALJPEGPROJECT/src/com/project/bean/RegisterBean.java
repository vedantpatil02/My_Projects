package com.project.bean;

import java.io.InputStream;

public class RegisterBean {
	private String uname,uaddress,uemail,umobno,upassword, cpassword;
    public int uid;
	private InputStream userpic;
    
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getUaddress() {
		return uaddress;
	}

	public void setUaddress(String uaddress) {
		this.uaddress = uaddress;
	}

	public String getUemail() {
		return uemail;
	}

	public void setUemail(String uemail) {
		this.uemail = uemail;
	}

	public String getUmobno() {
		return umobno;
	}

	public void setUmobno(String umobno) {
		this.umobno = umobno;
	}

	public String getUpassword() {
		return upassword;
	}

	public void setUpassword(String upassword) {
		this.upassword = upassword;
	}

	public String getCpassword() {
		return cpassword;
	}

	public void setCpassword(String cpassword) {
		this.cpassword = cpassword;
	}

	public InputStream getUserpic() {
		return userpic;
	}

	public void setUserpic(InputStream userpic) {
		this.userpic = userpic;
	}
	
	
}
