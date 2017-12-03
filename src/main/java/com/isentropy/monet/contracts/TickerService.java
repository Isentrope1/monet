package com.isentropy.monet.contracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes6;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.1.1.
 */
public final class TickerService extends Contract {
    private static final String BINARY = "6060604052613840600455341561001557600080fd5b336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555042600381905550610e0a8061006b6000396000f3006060604052600436106100c5576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806331b6c6b3146100ca57806332040af3146100ed5780633fe532aa146101655780634180ed65146101c957806358607048146101f2578063728b392c1461021b57806379e7b373146102445780638da5cb5b14610296578063976797ed146102eb578063abfafda41461035a578063da20ede014610434578063e613a50614610488578063fe45944e146104d1575b600080fd5b34156100d557600080fd5b6100eb60048080359060200190919050506104f4565b005b61014f600480803579ffffffffffffffffffffffffffffffffffffffffffffffffffff191690602001909190803579ffffffffffffffffffffffffffffffffffffffffffffffffffff1916906020019091908035906020019091905050610559565b6040518082815260200191505060405180910390f35b341561017057600080fd5b6101c760048080357effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff191690602001909190803573ffffffffffffffffffffffffffffffffffffffff1690602001909190505061078a565b005b34156101d457600080fd5b6101dc61084c565b6040518082815260200191505060405180910390f35b34156101fd57600080fd5b610205610852565b6040518082815260200191505060405180910390f35b341561022657600080fd5b61022e610858565b6040518082815260200191505060405180910390f35b610280600480803579ffffffffffffffffffffffffffffffffffffffffffffffffffff191690602001909190803590602001909190505061085e565b6040518082815260200191505060405180910390f35b34156102a157600080fd5b6102a96109cf565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b610344600480803579ffffffffffffffffffffffffffffffffffffffffffffffffffff191690602001909190803579ffffffffffffffffffffffffffffffffffffffffffffffffffff19169060200190919050506109f4565b6040518082815260200191505060405180910390f35b341561036557600080fd5b6104326004808035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509190803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091905050610a0b565b005b341561043f57600080fd5b610472600480803579ffffffffffffffffffffffffffffffffffffffffffffffffffff1916906020019091905050610c35565b6040518082815260200191505060405180910390f35b6104bb600480803579ffffffffffffffffffffffffffffffffffffffffffffffffffff1916906020019091905050610c4d565b6040518082815260200191505060405180910390f35b34156104dc57600080fd5b6104f26004808035906020019091905050610c62565b005b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561054f57600080fd5b8060048190555050565b6000806000806005543410156105d1573373ffffffffffffffffffffffffffffffffffffffff166108fc349081150290604051600060405180830381858888f1935050505015156105a957600080fd5b7f01000000000000000000000000000000000000000000000000000000000000009350610780565b6105db8786610cc7565b925060007fff000000000000000000000000000000000000000000000000000000000000008416141515610673573373ffffffffffffffffffffffffffffffffffffffff166108fc349081150290604051600060405180830381858888f19350505050151561064957600080fd5b7fff0000000000000000000000000000000000000000000000000000000000000083169350610780565b61067d8686610cc7565b915060007fff000000000000000000000000000000000000000000000000000000000000008316141515610715573373ffffffffffffffffffffffffffffffffffffffff166108fc349081150290604051600060405180830381858888f1935050505015156106eb57600080fd5b7fff0000000000000000000000000000000000000000000000000000000000000082169350610780565b600554341115610764573373ffffffffffffffffffffffffffffffffffffffff166108fc60055434039081150290604051600060405180830381858888f19350505050151561076357600080fd5b5b816080849060020a0281151561077657fe5b0490508060001793505b5050509392505050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156107e557600080fd5b7f5900000000000000000000000000000000000000000000000000000000000000827effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff19161415610848578073ffffffffffffffffffffffffffffffffffffffff16ff5b5050565b60035481565b60055481565b60045481565b6000806005543410156108d3573373ffffffffffffffffffffffffffffffffffffffff166108fc349081150290604051600060405180830381858888f1935050505015156108ab57600080fd5b7f010000000000000000000000000000000000000000000000000000000000000091506109c8565b6108dd8484610cc7565b905060007fff000000000000000000000000000000000000000000000000000000000000008216141515610975573373ffffffffffffffffffffffffffffffffffffffff166108fc349081150290604051600060405180830381858888f19350505050151561094b57600080fd5b7fff00000000000000000000000000000000000000000000000000000000000000811691506109c8565b6005543411156109c4573373ffffffffffffffffffffffffffffffffffffffff166108fc60055434039081150290604051600060405180830381858888f1935050505015156109c357600080fd5b5b8091505b5092915050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000610a038383600454610559565b905092915050565b6000806000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610a6957600080fd5b600091505b8451821015610c27578382815181101515610a8557fe5b90602001906020020151600160008785815181101515610aa157fe5b9060200190602002015179ffffffffffffffffffffffffffffffffffffffffffffffffffff191679ffffffffffffffffffffffffffffffffffffffffffffffffffff19168152602001908152602001600020819055508451835114610b065742610b1f565b8282815181101515610b1457fe5b906020019060200201515b905080600260008785815181101515610b3457fe5b9060200190602002015179ffffffffffffffffffffffffffffffffffffffffffffffffffff191679ffffffffffffffffffffffffffffffffffffffffffffffffffff19168152602001908152602001600020819055507f4592a0b20ea053be56890ca74757d6bae8552f2686948fc0a90e539e59ea3b3f8583815181101515610bb957fe5b9060200190602002015182604051808379ffffffffffffffffffffffffffffffffffffffffffffffffffff191679ffffffffffffffffffffffffffffffffffffffffffffffffffff191681526020018281526020019250505060405180910390a18180600101925050610a6e565b426003819055505050505050565b60026020528060005260406000206000915090505481565b6000610c5b8260045461085e565b9050919050565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610cbd57600080fd5b8060058190555050565b6000806000600160008679ffffffffffffffffffffffffffffffffffffffffffffffffffff191679ffffffffffffffffffffffffffffffffffffffffffffffffffff191681526020019081526020016000205491506000821415610d4d577f02000000000000000000000000000000000000000000000000000000000000009250610dd6565b600260008679ffffffffffffffffffffffffffffffffffffffffffffffffffff191679ffffffffffffffffffffffffffffffffffffffffffffffffffff19168152602001908152602001600020549050808442031115610dcf577f03000000000000000000000000000000000000000000000000000000000000009250610dd6565b8160001792505b5050929150505600a165627a7a72305820ac2660ccbf4aa758786e60fd54fcc6ee7993f4b92b2b7c57aa304f64cd0ebd520029";

    private TickerService(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private TickerService(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<PriceUpdatedEventResponse> getPriceUpdatedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("PriceUpdated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes6>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<PriceUpdatedEventResponse> responses = new ArrayList<PriceUpdatedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            PriceUpdatedEventResponse typedResponse = new PriceUpdatedEventResponse();
            typedResponse.symbol = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PriceUpdatedEventResponse> priceUpdatedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("PriceUpdated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes6>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PriceUpdatedEventResponse>() {
            @Override
            public PriceUpdatedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                PriceUpdatedEventResponse typedResponse = new PriceUpdatedEventResponse();
                typedResponse.symbol = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<TransactionReceipt> set_default_max_age(BigInteger max) {
        Function function = new Function(
                "set_default_max_age", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(max)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> lookup_exchange_rate_maxage(byte[] symbol, byte[] insymbol, BigInteger max_age, BigInteger weiValue) {
        Function function = new Function(
                "lookup_exchange_rate_maxage", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes6(symbol), 
                new org.web3j.abi.datatypes.generated.Bytes6(insymbol), 
                new org.web3j.abi.datatypes.generated.Uint256(max_age)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> self_destruct(byte[] sure, String send_balance_to) {
        Function function = new Function(
                "self_destruct", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes1(sure), 
                new org.web3j.abi.datatypes.Address(send_balance_to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> last_update() {
        Function function = new Function("last_update", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> query_cost() {
        Function function = new Function("query_cost", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> default_max_age() {
        Function function = new Function("default_max_age", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> lookup_value_in_wei_maxage(byte[] symbol, BigInteger max_age, BigInteger weiValue) {
        Function function = new Function(
                "lookup_value_in_wei_maxage", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes6(symbol), 
                new org.web3j.abi.datatypes.generated.Uint256(max_age)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<String> owner() {
        Function function = new Function("owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> lookup_exchange_rate(byte[] symbol, byte[] insymbol, BigInteger weiValue) {
        Function function = new Function(
                "lookup_exchange_rate", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes6(symbol), 
                new org.web3j.abi.datatypes.generated.Bytes6(insymbol)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> report_values(List<byte[]> symbols, List<BigInteger> values, List<BigInteger> timestamps) {
        Function function = new Function(
                "report_values", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes6>(
                        org.web3j.abi.Utils.typeMap(symbols, org.web3j.abi.datatypes.generated.Bytes6.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(values, org.web3j.abi.datatypes.generated.Uint256.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Uint256>(
                        org.web3j.abi.Utils.typeMap(timestamps, org.web3j.abi.datatypes.generated.Uint256.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> last_update_of_symbol(byte[] param0) {
        Function function = new Function("last_update_of_symbol", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes6(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> lookup_value_in_wei(byte[] symbol, BigInteger weiValue) {
        Function function = new Function(
                "lookup_value_in_wei", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes6(symbol)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteCall<TransactionReceipt> set_query_cost(BigInteger _qc) {
        Function function = new Function(
                "set_query_cost", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_qc)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<TickerService> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TickerService.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<TickerService> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(TickerService.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static TickerService load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new TickerService(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static TickerService load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new TickerService(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class PriceUpdatedEventResponse {
        public byte[] symbol;

        public BigInteger timestamp;
    }
}
