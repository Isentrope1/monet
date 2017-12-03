package com.isentropy.monet.contracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Int256;
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
public final class StockTicker extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506105c68061005e6000396000f300606060405260043610610041576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806336f2f60014610046575b600080fd5b341561005157600080fd5b6101a7600480803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091908035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919080359060200190820180359060200190808060200260200160405190810160405280939291908181526020018383602002808284378201915050505050509190803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091908035906020019082018035906020019080806020026020016040519081016040528093929190818152602001838360200280828437820191505050505050919080359060200190919050506101a9565b005b6000806000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141561037b5787519150600090505b8181101561037a57878181518110151561022057fe5b90602001906020020151600019167f82954654fbd82943bf1d6e1b609142dcfd41ddc8216c778dfdc5dc93da4f2d7d61026f8a8481518110151561026057fe5b90602001906020020151610385565b858a8581518110151561027e57fe5b906020019060200201518a8681518110151561029657fe5b906020019060200201518a878151811015156102ae57fe5b906020019060200201518a888151811015156102c657fe5b906020019060200201516040518080602001878152602001868152602001858152602001848152602001838152602001828103825288818151815260200191508051906020019080838360005b8381101561032e578082015181840152602081019050610313565b50505050905090810190601f16801561035b5780820380516001836020036101000a031916815260200191505b5097505050505050505060405180910390a2808060010191505061020a565b5b5050505050505050565b61038d610572565b610395610586565b60008060006103a2610586565b60206040518059106103b15750595b9080825280601f01601f1916602001820160405250945060009350600092505b602083101561048f578260080260020a876001900402600102915060007f010000000000000000000000000000000000000000000000000000000000000002827effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff19161415156104825781858581518110151561044957fe5b9060200101907effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916908160001a90535083806001019450505b82806001019350506103d1565b8360405180591061049d5750595b9080825280601f01601f19166020018201604052509050600092505b838310156105655784838151811015156104cf57fe5b9060200101517f010000000000000000000000000000000000000000000000000000000000000090047f010000000000000000000000000000000000000000000000000000000000000002818481518110151561052857fe5b9060200101907effffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff1916908160001a90535082806001019350506104b9565b8095505050505050919050565b602060405190810160405280600081525090565b6020604051908101604052806000815250905600a165627a7a72305820db87026987e7bdaf761de77ae98ccc4513529c7d0e349079511dc567c85707130029";

    private StockTicker(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private StockTicker(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<PriceEventResponse> getPriceEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Price", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Int256>() {}, new TypeReference<Int256>() {}, new TypeReference<Int256>() {}, new TypeReference<Int256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<PriceEventResponse> responses = new ArrayList<PriceEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            PriceEventResponse typedResponse = new PriceEventResponse();
            typedResponse.symbolIdx = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.symbol = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.open = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.high = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.low = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.close = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PriceEventResponse> priceEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Price", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Int256>() {}, new TypeReference<Int256>() {}, new TypeReference<Int256>() {}, new TypeReference<Int256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PriceEventResponse>() {
            @Override
            public PriceEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                PriceEventResponse typedResponse = new PriceEventResponse();
                typedResponse.symbolIdx = (byte[]) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.symbol = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.open = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.high = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.low = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.close = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<TransactionReceipt> setPrices(List<byte[]> symbol, List<BigInteger> open, List<BigInteger> high, List<BigInteger> low, List<BigInteger> close, BigInteger time) {
        Function function = new Function(
                "setPrices", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.Utils.typeMap(symbol, org.web3j.abi.datatypes.generated.Bytes32.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Int256>(
                        org.web3j.abi.Utils.typeMap(open, org.web3j.abi.datatypes.generated.Int256.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Int256>(
                        org.web3j.abi.Utils.typeMap(high, org.web3j.abi.datatypes.generated.Int256.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Int256>(
                        org.web3j.abi.Utils.typeMap(low, org.web3j.abi.datatypes.generated.Int256.class)), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Int256>(
                        org.web3j.abi.Utils.typeMap(close, org.web3j.abi.datatypes.generated.Int256.class)), 
                new org.web3j.abi.datatypes.generated.Uint256(time)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<StockTicker> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(StockTicker.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<StockTicker> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(StockTicker.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static StockTicker load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new StockTicker(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static StockTicker load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new StockTicker(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class PriceEventResponse {
        public byte[] symbolIdx;

        public String symbol;

        public BigInteger time;

        public BigInteger open;

        public BigInteger high;

        public BigInteger low;

        public BigInteger close;
    }
}
