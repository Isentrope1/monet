package com.isentropy.monet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.ObjectMapperFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isentropy.monet.contracts.Token;

public class MonetOptions {
	public static final String TOKEN_DEF_FILE_NAME ="tokens.tsv";
	public static final String CONACTS_FILE_NAME ="contacts.tsv";
	public static final String KEYDIR_NAME ="keys";
	
	private static Logger log = LoggerFactory.getLogger(com.isentropy.monet.MonetOptions.class);
	protected BigInteger gas_limit_sendtoken = BigInteger.valueOf(210000);
	protected BigInteger gas_limit_deploytoken = Token.GAS_LIMIT;
	
	protected String rpc;//="http://localhost:8545";
	//protected String user;
	//protected String pass;
	protected String datadir;
	//protected String ticker_file;
	protected int min_passwd_length = 4;
	protected HashMap<String,ERC20> token_definitions = new HashMap<String,ERC20>();

	//key is lc(display name)
	private Map<String,Contact> contacts = new TreeMap<String,Contact>();
	//value is display name
	private Map<String,String> contact_addr2name = new  HashMap<String,String>();
	protected HashMap<String,WalletFile> wallets = new HashMap<String,WalletFile>();
	
	public String getDataDir(){
		return datadir;
	}
	public Map<String,Contact> getContacts(){
		return contacts;
	}
	public String getNameFromAddress(String addr){
		return contact_addr2name.get(addr);
	}
	public String getContactAddress(String name){
		return contacts.get(name.toLowerCase()).getAddress();
	}
	private void addToken(ERC20 tok){
		token_definitions.put(tok.getSymbol(),tok);
	}
	protected int loadFileTokenDefinitions(Reader r) throws IOException{
		BufferedReader br = new BufferedReader(r);
		String line;
		int n=0;
		while((line=br.readLine()) != null){
			if(line.startsWith("#"))
				continue;
			String[] f=line.split("\t");
			if(f.length >= 4 && !f[3].equals("")){
				//with description
				addToken(new ERC20(f[0],f[1],Integer.parseInt(f[2]),f[3]));				
			}
			else{
				addToken(new ERC20(f[0],f[1],Integer.parseInt(f[2])));								
			}
			n++;
		}
		return n;
	}
	protected int writeFileTokenDefinitions(Writer w) throws IOException{
		int n=0;
		for(Map.Entry<String,ERC20> e : token_definitions.entrySet()){
			ERC20 t = e.getValue();
			String desc = t.getDescription() == null ? "": t.getDescription();
			w.write(t.getSymbol()+"\t"+t.getAddress()+"\t"+t.getDecimals()+"\t"+desc+"\n");
			n++;
		}
		w.flush();
		return n;
	}
	protected void addContact(String name,String addr){
		addr = addr.toLowerCase();
		contacts.put(name.toLowerCase(),new Contact(name,addr));
		contact_addr2name.put(addr,name);
	}
		
	protected int loadContactDefinitions(Reader r) throws IOException{
		BufferedReader br = new BufferedReader(r);
		String line;
		int n=0;
		while((line=br.readLine()) != null){
			if(line.startsWith("#"))
				continue;
			String[] f=line.split("\t");
			addContact(f[0],f[1]);
			n++;
		}
		return n;
	}
	
	protected int writeContactDefinitions(Writer w) throws IOException{
		int n=0;
		for(Map.Entry<String,Contact> e : contacts.entrySet()){
			Contact c = e.getValue();
			w.write(c.getDisplayName()+"\t"+c.getAddress()+"\n");
			n++;
		}
		w.flush();
		return n;
	}
	
	protected void loadKeys(File keydir) throws JsonParseException, JsonMappingException, IOException{
		File[] files = keydir.listFiles();
		for(File f : files){
			ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
			WalletFile wallet = objectMapper.readValue(f, WalletFile.class);
			wallets.put("0x"+wallet.getAddress(), wallet);
			log.info("Added wallet: " +wallet.getAddress());
		}
	}
	protected void writeKeys(File keydir) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
		for(Map.Entry<String, WalletFile> e: wallets.entrySet()){
			objectMapper.writeValue(new File(keydir.getAbsolutePath()+'/'+e.getKey()), e.getValue());
		}
	}
	public void save() throws JsonParseException, JsonMappingException, IOException{
		writeKeys(getKeyDir());
		writeFileTokenDefinitions(new OutputStreamWriter(new FileOutputStream(getTokenDefFile()),StandardCharsets.UTF_8));
		writeContactDefinitions(new OutputStreamWriter(new FileOutputStream(getContactsFile()),StandardCharsets.UTF_8));
	}
	
	public MonetOptions(String datadir_) throws FileNotFoundException, IOException {
		rpc = getDefaultIPC();
		init(datadir_);
	}
	public MonetOptions() throws FileNotFoundException, IOException {
		rpc = getDefaultIPC();
		init(System.getProperty("user.home")+"/.monet");
	}
	public File getKeyDir(){
		return new File(datadir+"/"+KEYDIR_NAME);
	}
	public File getContactsFile(){
		return new File(datadir+"/"+CONACTS_FILE_NAME);
	}
	public File getTokenDefFile(){
		return new File(datadir+"/"+TOKEN_DEF_FILE_NAME);
	}
	private String getDefaultIPC(){
	/*
	 * see https://ethereum.stackexchange.com/questions/1492/when-is-the-geth-ipc-file-produced
	 */
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("win")){
			return System.getProperty("user.home")+"/AppData/Roaming/Ethereum/geth.ipc";
		}
		else if(os.contains("nux") || os.contains("nix")){
			return System.getProperty("user.home")+"/.ethereum/geth.ipc";
		}
		else if(os.contains("mac")){
			return System.getProperty("user.home")+"/Library/Ethereum/geth.ipc";
		}
		return System.getProperty("user.home")+"/.ethereum/geth.ipc";
	}
	public void refreshConfig() throws FileNotFoundException, IOException{
		init(datadir);
	}
	private void init(String datadir_) throws FileNotFoundException, IOException {
		datadir = datadir_;
		File dd = new File(datadir);
		if(!dd.exists())
			dd.mkdirs();
		File tdef = getTokenDefFile();
		if(tdef.exists()){
			loadFileTokenDefinitions(new InputStreamReader(new FileInputStream(tdef),StandardCharsets.UTF_8));
		}
		else{
			log.info("loading hard coded tokens");
			loadDefaultTokens();	
			writeFileTokenDefinitions(new OutputStreamWriter(new FileOutputStream(getTokenDefFile()),StandardCharsets.UTF_8));
			log.info("loading hard coded tokens");
		}

		File cdef = getContactsFile(); 
		if(cdef.exists()){
			loadContactDefinitions(new InputStreamReader(new FileInputStream(cdef),StandardCharsets.UTF_8));
		}
		
		File keydir = getKeyDir();
		if(keydir.exists()){
			loadKeys(keydir);
		}
		else{
			keydir.mkdirs();
		}
			
	}
	/**
	 * these tokens are loaded by default into tokens.tsv
	 * when creating the conf dir
	 */
	protected void loadDefaultTokens() {
		addToken(new ERC20("REP","0xe94327d07fc17907b4db788e5adf2ed424addff6",18,"Auger"));
		addToken(new ERC20("OMG","0xd26114cd6EE289AccF82350c8d8487fedB8A0C07",18,"OmiseGO"));
		addToken(new ERC20("GNO","0x6810e776880c02933d47db1b9fc05908e5386b96",18,"Gnosis"));
		addToken(new ERC20("EOS","0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0",18,"EOS"));
		addToken(new ERC20("QTUM","0x9a642d6b3368ddc662CA244bAdf32cDA716005BC",18,"Qtum"));
		addToken(new ERC20("GNT","0xa74476443119A942dE498590Fe1f2454d7D4aC0d",18,"Golem"));
	}

}
