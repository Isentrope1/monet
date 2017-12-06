package com.isentropy.monet;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.exceptions.ContractCallException;
import org.web3j.utils.Convert;
import org.web3j.utils.Convert.Unit;

import com.isentropy.monet.contracts.HumanStandardToken;
import com.isentropy.monet.contracts.HumanStandardTokenFactory;
import com.isentropy.monet.contracts.Token;

public class Monet {
	private static Logger log = LoggerFactory.getLogger(com.isentropy.monet.Monet.class);

	public final static String ETH ="ETH";

	public final static String COUNT_DE_MONET_NAME ="CountDeMonet";
	public final static String COUNT_DE_MONET_ADDRESS ="0x978097f6628b66b22df8b8d39284743c58e1542f";

	public final static String MONET_TOKEN_NAME ="MONET";
	public final static String MONET_TOKEN_ADDRESS ="0x5648549517cbedcb366898aedd55f4e032dc9c33";
	public final static String DOC_URL ="https://github.com/isentropy/monet";

	public final static String WARNING = "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE";
	public final static String INFO="\nMonet v0.1 by Isentropy (https://isentropy.com)\n"
			+"\nA simple, open-source command line wallet for trading, tracking and creating Etherum tokens.\n"
			+"\nLicensed under Modified MIT License (see LICENSE file)\n\n"
			+ WARNING
			+"\n\nCount de Monet likes tokens:\n"+COUNT_DE_MONET_ADDRESS
			+"\nor send over Monet:\nsend "+COUNT_DE_MONET_NAME + " <amount> <token name>";

	protected final static DefaultBlockParameter latest = DefaultBlockParameter.valueOf("latest");
	protected final static BigDecimal eth_to_wei = BigDecimal.TEN.pow(18);
	protected MonetOptions opts;
	protected Service svc;
	protected Web3j web3;
	protected PriceChecker pc;
	private final DecimalFormat four_digits = new DecimalFormat(".####");
	private final DecimalFormat two_digits = new DecimalFormat(".##");
	private final DecimalFormat no_digits = new DecimalFormat(".");

	private BigInteger gas_price = null;
	private Pattern ethaddress = Pattern.compile("0x[A-Za-z0-9]{40,40}");
	protected static class Position{
		public Position(String symb, double q){
			symbol = symb;
			quantity=q;
		}
		public final String symbol;
		public double quantity;
	}

	public Monet(MonetOptions opts_) throws IOException {
		this.opts = opts_;

		if(!opts.rpc.startsWith("http://") && !opts.rpc.startsWith("https://")){
			log.info("Starting IPC service to "+opts.rpc+" ...");
			svc = new UnixIpcService(opts.rpc);						
			log.info("done");
		}
		else{
			log.info("Starting RPC service to "+opts.rpc+" ...");
			svc = new HttpService(opts.rpc);			
			log.info("done");
		}
		web3 = Web3j.build(svc);
		refreshPriceChecker();
	}
	protected void refreshGasPrice() throws IOException{
		Request<?, EthGasPrice> gp = web3.ethGasPrice();
		gas_price = gp.send().getGasPrice();
		log.info("Gas price: "+gas_price.toString());
	}
	public BigInteger getGasPrice() throws IOException{
		if(gas_price == null)
			refreshGasPrice();
		return gas_price;
	}

	protected void refreshPriceChecker() throws IOException{
		pc = PriceChecker.fromCoinMarketCapJson();
	}

	public static MonetOptions parseArgs(String[] args) throws FileNotFoundException, IOException{
		String datadir=null;
		for(int i=0;i<args.length;i++){
			if(args[i].equals("--datadir"))
				datadir=args[++i];
			if(args[i].equals("--help")){
				argsHelp();
				System.exit(0);
			}

		}
		MonetOptions o = datadir == null ? new MonetOptions() : new MonetOptions(datadir);
		for(int i=0;i<args.length;i++){
			if(args[i].equals("--rpc"))
				o.rpc=args[++i];
		}
		return o;
	}
	public Map<String,Collection<Position>> getPositions() throws Exception{
		return getPositions(null,null);
	}	
	public Map<String,Collection<Position>> getPositions(Collection<String> accts, Map<String,ERC20> tokens) throws Exception{
		HashMap<String,Collection<Position>> positions = new HashMap<String,Collection<Position>>();
		if(accts == null){
			accts= opts.wallets.keySet();
		}
		if(tokens == null)
			tokens = opts.token_definitions;

		for(String acct : accts){
			EthGetBalance bal = web3.ethGetBalance(acct, latest).send();
			BigInteger eth_i = bal.getBalance();
			if(eth_i.compareTo(BigInteger.ZERO) > 0){
				BigDecimal eth_d = new BigDecimal(eth_i).divide(eth_to_wei);
				Collection<Position> p = positions.get(acct);
				if(p == null){
					p = new ArrayList<Position>();
					positions.put(acct, p);
				}
				p.add(new Position(ETH,eth_d.doubleValue()));
			}
			TransactionManager tm = new ClientTransactionManager(web3,acct);
			for(ERC20 tok : tokens.values()){
				Token t;
				BigInteger bal_i;
				try{
					t = Token.load(tok.getAddress(), web3, tm, Token.GAS_PRICE, Token.GAS_LIMIT);
					bal_i = t.balanceOf(acct).send();
				}
				catch(ContractCallException e){
					log.warn("Bad Token address: "+tok.getAddress());
					log.warn(e.getMessage());
					continue;
				}
				if(bal_i.compareTo(BigInteger.ZERO) <= 0)
					continue;
				BigDecimal bal_d = new BigDecimal(bal_i).divide(BigDecimal.TEN.pow(tok.getDecimals()));
				Collection<Position> p = positions.get(acct);
				if(p == null){
					p = new ArrayList<Position>();
					positions.put(acct, p);
				}
				p.add(new Position(tok.getSymbol(),bal_d.doubleValue()));
			}
		}
		return positions;
	}

	public static Map<String,Double> getTotals(Map<String,Collection<Position>> positions){
		Map<String,Double> totals = new HashMap<String,Double>();
		for(Map.Entry<String,Collection<Position>> e : positions.entrySet()){
			String acct = e.getKey();
			Collection<Position> pos = e.getValue();
			for(Position p : pos){
				Double tot = totals.get(p.symbol);
				if(tot == null)
					totals.put(p.symbol, p.quantity);
				else
					totals.put(p.symbol, tot+p.quantity);
			}
		}
		return totals;
	}
	protected String addressWithName(String address,int display_chars){
		String name = opts.getNameFromAddress(address);
		String abbrevadd = address;
		if(display_chars >= 0 && display_chars < address.length())
			abbrevadd = address.substring(0, display_chars);

		if(name == null)
			return abbrevadd;

		return name+" ["+abbrevadd+"]";
	}
	protected void printTokens(PrintWriter ps){
		Double val = pc.getPrice(ETH);
		String fmt = "%-10s\t%s\t%-20s\t%s\n";
		ps.printf(fmt,"TOKEN","RATE","DESC","ADDRESS");
		ps.printf(fmt,ETH,(val==null?"":'$'+two_digits.format(val)),"Ether","");
		for(ERC20 e : opts.token_definitions.values()){
			val = pc.getPrice(e.getSymbol());
			String desc = e.getDescription();
			if(desc == null)
				desc = "";
			ps.printf(fmt,e.getSymbol(),(val==null?"":'$'+two_digits.format(val)),desc, e.getAddress());
		}
		ps.flush();
	}
	protected void printContacts(PrintWriter ps){
		String fmt = "%-20s\t%s\n";
		ps.printf(fmt,"CONTACT NAME","ADDRESS");
		for(Map.Entry<String, Contact> e : opts.getContacts().entrySet()){
			Contact c = e.getValue();
			ps.printf(fmt,c.getDisplayName(),c.getAddress());
		}
		ps.flush();
	}
	protected void printWalletNames(Console cons) throws Exception{
		String fmt = "%-20s\t%42s\n";
		cons.printf(fmt,"WALLET_NAME","ADDRESS");
		for(Map.Entry<String,WalletFile> e : opts.wallets.entrySet()){
			String addr = e.getKey();
			String name = opts.getNameFromAddress(addr);
			cons.printf(fmt,name==null?"":name,addr);
		}
	}
	protected void printWalletBalances(Console cons) throws Exception{
		String fmt = "%-30s\t%14s\t%14s\t%s\n";
		cons.printf(fmt,"WALLET","TOKEN","HOLDINGS","VALUE");
		Map<String,Collection<Position>> positions = getPositions();
		for(Map.Entry<String,Collection<Position>> e : positions.entrySet()){
			String address = e.getKey();
			Collection<Position> pos = e.getValue();
			for(Position p: pos){
				Double rate = pc.getPrice(p.symbol);
				String usd = rate == null ? "":"$"+no_digits.format(rate*p.quantity);
				cons.printf(fmt,addressWithName(address,10),p.symbol,four_digits.format(p.quantity),usd);
			}
		}
	}
	protected void printTotals(Map<String,Double> totals, PrintWriter ps){
		double usd=0;
		String fmt = "%-20s\t%15s\t%s\t%s\n";
		ps.printf(fmt,"TOKEN","HOLDINGS","RATE","VALUE");
		for(Map.Entry<String,Double> e : totals.entrySet()){
			String symb = e.getKey();
			double bal = e.getValue();
			Double rate=pc.getPrice(symb);
			String f3="",f4="";
			if(rate != null){
				double valusd = bal*rate;
				usd += valusd;
				f3="$"+two_digits.format(rate);
				f4="$"+no_digits.format(valusd); 
			}
			ps.printf(fmt,symb,two_digits.format(bal),f3,f4);
		}
		ps.printf(fmt,"TOTAL_USD","","",'$'+no_digits.format(usd));
		ps.flush();
	}

	protected Credentials getAccountWithMinBal(double amount,String currency,Console cons) throws Exception{
		Map<String,Collection<Position>> positions = getPositions();
		ArrayList<String> possible_accts = new ArrayList<String>();
		int i=1;
		for(Map.Entry<String,Collection<Position>> e : positions.entrySet()){
			String acct = e.getKey();
			Collection<Position> pos = e.getValue();
			for(Position p : pos){
				if(p.symbol.equals(currency) && p.quantity >= amount){
					possible_accts.add(acct);
					if(i==1)
						cons.printf("\nAvailable payment wallets:\n");
					cons.printf("%d: %s, available %s: %f\n", i++,addressWithName(acct,10),p.symbol,p.quantity);
				}
				else{
					//cons.printf("%s %f",p.symbol,p.quantity);
				}
			}
		}
		if(possible_accts.size() == 0){
			cons.printf("No account found with %f %s\n",amount,currency);
			return null;
		}
		String s=cons.readLine("Pay from which wallet? ");
		if(s.trim().equals(""))
			return null;
		int acctnum = Integer.parseInt(s);
		String payacct = possible_accts.get(acctnum-1);
		cons.printf("Paying %f %s from:\n%s\n",amount,currency, addressWithName(payacct,10));
		char[] pw = cons.readPassword("Enter acct password to confirm: ");
		if(pw == null || pw.length == 0)
			return null;
		WalletFile wf = opts.wallets.get(payacct);
		ECKeyPair kp = Wallet.decrypt(new String(pw), wf);
		Credentials creds = Credentials.create(kp);
		return creds;
	}

	protected String createToken(String currency,double amount, Console cons) throws Exception{
		int decimals = 18;
		BigInteger gp = getGasPrice();
		double fee_eth = new BigDecimal(gp.multiply(opts.gas_limit_deploytoken)).divide(BigDecimal.TEN.pow(18)).doubleValue();
		cons.printf("Create Token transaction costs a maximum of %f ETH\n",fee_eth);

		Credentials creds = getAccountWithMinBal(fee_eth,ETH,cons);
		if(creds == null)
			return null;
		TransactionManager tm = new RawTransactionManager(web3,creds);
		ERC20 erc20 = opts.token_definitions.get(currency);

		BigInteger amt = BigDecimal.valueOf(amount).multiply(BigDecimal.TEN.pow(decimals)).toBigInteger();
		RemoteCall<HumanStandardToken> deploy = 
				HumanStandardToken.deploy(web3, creds, gp, opts.gas_limit_deploytoken, amt, currency, BigInteger.valueOf(decimals), currency);
		//HumanStandardToken.deploy(web3, creds, tm, HumanStandardToken.GAS_LIMIT.multiply(BigInteger.valueOf(9999999)), amount, currency, BigInteger.valueOf(decimals), currency);
		cons.printf("Sending...");
		HumanStandardToken newtok = deploy.send();
		if(newtok == null || newtok.getContractAddress() == null){
			cons.printf("Error creating token.\n");
			return null;
		}
		String addr = newtok.getContractAddress();
		cons.printf("done\n");
		opts.token_definitions.put(currency, new ERC20(currency,addr,decimals));
		opts.save();
		return addr;
	}
	protected void displayTransaction(TransactionReceipt r,Console cons) throws IOException{
		BigInteger gas = r.getGasUsed();
		BigDecimal gas_used_in_eth=new BigDecimal(getGasPrice().multiply(gas)).divide(eth_to_wei);
		
		cons.printf("Transaction:\tgas_used_eth=%s\tindex=%s\tblock_num=%s\tgas_used=%s\tstatus=%s\thash=%s\n",gas_used_in_eth.toPlainString(),r.getTransactionIndexRaw(), r.getBlockNumber(), r.getGasUsed().toString(),r.getStatus(),r.getTransactionHash());
	}
	protected byte send(String recip,double amount,String currency,Console cons) throws Exception{
		BigInteger gp = getGasPrice();
		double fee_eth = new BigDecimal(gp.multiply(opts.gas_limit_sendtoken)).divide(BigDecimal.TEN.pow(18)).doubleValue();
		cons.printf("This send transaction costs a maximum of %f ETH\n",fee_eth);
		cons.printf("\nSending to:\n%s\n\n",addressWithName(recip, -1));
		Credentials creds = getAccountWithMinBal(amount,currency,cons);
		if(creds == null){
			cons.printf("Aborted send.\n");
			return 1;
		}
		if(recip.equals(creds.getAddress())){
			cons.printf("\nAborted send. Sender and recipient must be different.\n");
			return 2;			
		}
		TransactionReceipt receipt;
		cons.printf("Sending...");
		if(currency.equals(ETH)){
			TransactionManager tm = new RawTransactionManager(web3,creds);
			Transfer trans = new Transfer(web3, tm);
			receipt = trans.sendFunds(recip, BigDecimal.valueOf(amount), Unit.ETHER, getGasPrice(),opts.gas_limit_sendtoken).send();
		}
		else{
			TransactionManager tm = new RawTransactionManager(web3,creds);
			ERC20 erc20 = opts.token_definitions.get(currency);
			Token t = Token.load(erc20.getAddress(), web3, tm, getGasPrice(), opts.gas_limit_sendtoken);
			RemoteCall<TransactionReceipt> rc = t.transfer(recip, new BigDecimal(amount).multiply(BigDecimal.TEN.pow(erc20.getDecimals())).toBigInteger());
			receipt = rc.send();
		}
		cons.printf("done\n");
		displayTransaction(receipt,cons);
		return 0;
	}
	protected byte createAccount(String acctname,Console cons) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CipherException, IOException{
		cons.printf("Creating account"+acctname==null?"":" "+acctname+"\n");
		char[] pw1 = cons.readPassword("Enter password for new account:");
		if(pw1.length < opts.min_passwd_length){
			cons.printf("Account creation aborted. Password must be %d chars.", opts.min_passwd_length);
			return 1;
		}
		char[] pw2 = cons.readPassword("Confirm password: ");
		if(!Arrays.equals(pw1, pw2)){
			cons.printf("Account creation aborted. Passwords dont match.");
			return 2;
		}
		ECKeyPair ecKeyPair = Keys.createEcKeyPair();
		WalletFile walletFile = Wallet.createStandard(new String(pw1), ecKeyPair);
		String address = "0x"+walletFile.getAddress();
		opts.wallets.put(address, walletFile);
		if(acctname != null){
			opts.addContact(acctname,address);
		}
		opts.save();
		cons.printf("Saved wallet %s as %s.\nRemember to back up data dir %s\n",address,acctname,opts.datadir);

		return 0;
	}

	public static void argsHelp(){
		System.out.println("Monet v0.1\nA command line wallet for creating and managing Ethereum tokens");
		System.out.println("Copyright 2017 Isentropy LLC (https://isentropy.com)");
		System.out.println("\n--datadir <dir>: dir to store config and wallets [default = ~/.monet]");
		System.out.println("--rpc <RPC URL or ipc file path>: IPC or RPC of geth");
		System.out.println("--help: display this help");

	}


	protected void help(PrintWriter ps) throws IOException{
		ps.println("\nHelp");
		ps.println("-------");
		ps.println("\nbalance [wallet]: print total or per wallet balance info");
		ps.println("\ncontact: list contacts");
		ps.println("contact add <name> <address>: add new contact address");
		ps.println("\nrefresh [config|prices]: refresh config or rates from coinmarketcap.com");
		ps.println("\nsend <address or contact> <amount> <currency>: send ETH or ERC20 token. Choose sending wallet from menu.");
		ps.println("\ntoken: token info");
		ps.println("token add <name> <address> [<decimals>]: define existing ERC20 token to watch");
		ps.println("token make <token_name> <total_amount>: create a new ERC20 token and push contract to blockchain. Choose recipient wallet from menu.");
		ps.println("\nwallet: per wallet balance info");
		ps.println("wallet make <name>: make a new wallet");
		ps.println("\ninfo: inform yourself");
		ps.println("\nset [<param> <value>]: view and set gas limits\n");
		ps.println("\nFull documentation at "+DOC_URL+"\n");
	}

	protected void info(PrintWriter out){
		out.println(INFO);
		out.println("\n---------------------------\n");
		out.println("\ndatadir = " + opts.datadir);
		out.println("connected to geth RPC at " + opts.rpc);
		out.println("price check URL: " +PriceChecker.COINMARKETCAP_API_V1_URL);
		out.println("\nFull documentation at "+DOC_URL+"\n");
	}

	protected void cli(Console cons) throws IOException{
		PrintWriter out = cons.writer();
		out.println(INFO);
		out.println("\n-----------------------------------------------");
		String prompt = "\n>> ";
		String line;
		while((line=cons.readLine(prompt)) != null){
			String[] fields = line.split("\\s+");
			try{
				String cmd = fields[0].toLowerCase();
				String sub = fields.length < 2 ? null : fields[1].toLowerCase();

				if(cmd.trim().equals(""))
					continue;
				byte rslt=-1;
				if("balance".startsWith(cmd)){
					if(sub == null || "totals".startsWith(sub)){
						Map<String,Collection<Position>> positions = getPositions();
						Map<String,Double> balances = getTotals(positions);
						printTotals(balances,out);
					}
					else if("wallet".startsWith(sub)){
						printWalletBalances(cons);
					}

				}
				else if("contacts".startsWith(cmd)){
					if(sub == null)
						printContacts(out);
					else if("add".startsWith(sub)){
						String name = fields[2];
						String address = fields[3];
						if(!ethaddress.matcher(address).matches()){
							cons.printf("%s is not a valid ethereum address",address);							
						}
						else{ 
							opts.addContact(name, address);
							opts.save();
							cons.printf("Saved contact %s. Remember to back up data dir %s\n",name,opts.datadir);
						}
					}
				}
				else if("tokens".startsWith(cmd)){
					if(sub == null)
						printTokens(out);
					else if("add".startsWith(sub)){
						String tokname = fields[2].toUpperCase();
						String addr = fields[3].toLowerCase();
						if(!ethaddress.matcher(addr).matches()){
							cons.printf("%s is not a valid address\n", addr);
							continue;
						}
						int decimals = 18;
						if(fields.length > 4)
							decimals = Integer.parseInt(fields[4]);
						opts.token_definitions.put(tokname, new ERC20(tokname, addr, decimals));
						opts.save();
						cons.printf("Added token definition for %s at address %s",tokname,addr);
					}
					else if("make".startsWith(sub)){
						String tokname = fields[2].toUpperCase();
						long amount = Long.parseLong(fields[3]);
						String taddress = createToken(tokname,amount,cons);
						if(taddress != null)
							cons.printf("New token %s successfully created at address %s\n", tokname,taddress);
					}
				}
				else if("send".startsWith(cmd)){
					String recip = fields[1];
					if(!recip.startsWith("0x")){
						String address = opts.getContactAddress(recip);
						if(address != null){
							log.info("Found address "+address+" for contact "+recip);
							recip = address;
						}
					}
					if(ethaddress.matcher(recip).matches()){
						Double amount = Double.parseDouble(fields[2]);
						String currency = fields[3].toUpperCase();
						rslt=send(recip,amount,currency,cons);						
					}
					else{
						cons.printf("%s is not a recognized contact or Ethereum address. see 'contacts'", recip);
					}

				}
				else if("info".startsWith(cmd)){
					info(out);
				}
				else if("refresh".startsWith(cmd)){
					boolean prices=false,conf=false;
					if(sub == null || "all".startsWith(sub)){
						prices=true;
						conf=true;
					}
					else if("prices".startsWith(sub)){
						prices=true;
					}
					else if("config".startsWith(sub)){
						conf=true;
					}
					if(prices){
						out.print("Refreshing prices from "+PriceChecker.COINMARKETCAP_API_V1_URL+" ...");
						refreshPriceChecker();
						out.println("done");
					}
					if(conf){
						out.print("Refreshing config...");
						opts.refreshConfig();
						out.println("done");
					}
				}
				else if("wallet".startsWith(cmd)){
					if(sub == null){
						printWalletNames(cons);
						cons.printf("\n\n");
						printWalletBalances(cons);
					}
					else if("make".startsWith(sub)){
						String acctname = null;
						if(fields.length > 2){
							acctname = fields[2];
						}
						rslt = createAccount(acctname,cons);
					}
				}
				else if("set".equals(cmd)){
					if(sub == null){
						cons.printf("\n%s = %s\n", "gas_limit_sendtoken",opts.gas_limit_sendtoken);
						cons.printf("%s = %s\n", "gas_limit_deploytoken",opts.gas_limit_deploytoken);
					}
					else if(sub.equals("gas_limit_sendtoken")){
						opts.gas_limit_sendtoken = new BigInteger(fields[2]);
					}
					else if(sub.equals("gas_limit_deploytoken")){
						opts.gas_limit_deploytoken = new BigInteger(fields[2]);
					}
					else{

					}
				}
				else if("quit".startsWith(cmd) ||
						"exit".equals(cmd)){
					return;
				}
				else{
					switch(cmd){
					case "help":
					case "?":help(out);break;			
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
				cons.printf("Error. Enter '?' or 'help' for help\n");
				//help(out);
			}
		}
	}
	public static void main(String[] args){
		MonetOptions o;
		Monet m=null;
		Console cons = System.console();
		try {
			o = parseArgs(args);
			m = new Monet(o);
		} catch (Exception e) {
			e.printStackTrace();
			cons.printf("\nCouldn't connect to RPC or IPC.\nDid you pass --rpc <RPC URL or ipc file> ?\n");
			System.exit(1);
		}
		try {
			m.cli(cons);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}

}
