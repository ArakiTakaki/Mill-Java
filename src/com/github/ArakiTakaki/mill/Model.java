package com.github.ArakiTakaki.mill;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * コンストラクタで this.primaryKet this.table を宣言する 複合主キーの場合は maltipleKey を指定する。
 * @author ArakiTakaki
 *
 */
public abstract class Model{
	protected String primaryKey = "id";
	protected String table = "";
	protected String[] maltipleKey = null;

	protected Model setMapping(ResultSet rs)throws SQLException{
		return null;
	}
	
	protected Model hasMany(Model model) {
		Mill millobj = new Mill();
		model.getPrimaryKey();
		return null;
	}
	
	public String getPrimaryKey() {
		return primaryKey;
	}
	
	public String getTable() {
		return table;
	}
	
	public String[] getMultipleKey() {
		return maltipleKey;
	}
	

}