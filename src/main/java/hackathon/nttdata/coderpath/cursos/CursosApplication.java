package hackathon.nttdata.coderpath.cursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.GsonBuilder;

import hackathon.nttdata.coderpath.cursos.blockchain.Block;
import hackathon.nttdata.coderpath.cursos.blockchain.BlockSegundo;
import hackathon.nttdata.coderpath.cursos.blockchain.StringUtil;
import hackathon.nttdata.coderpath.cursos.blockchain.Transaction;
import hackathon.nttdata.coderpath.cursos.blockchain.TransactionInput;
import hackathon.nttdata.coderpath.cursos.blockchain.TransactionOutput;
import hackathon.nttdata.coderpath.cursos.blockchain.Wallet;

import java.security.Security;
//import java.util.Base64;

//import com.google.gson.GsonBuilder;
import java.util.Map;

@EnableDiscoveryClient
@SpringBootApplication
public class CursosApplication {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static ArrayList<BlockSegundo> blockchainsegundo = new ArrayList<BlockSegundo>();
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();

	public static int difficulty = 3;
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA;
	public static Wallet walletB;
	public static Transaction genesisTransaction;

//	public static int difficulty = 5;

	public static void main(String[] args) {
		SpringApplication.run(CursosApplication.class, args);
		// add our blocks to the blockchain ArrayList:
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // Setup Bouncey castle as a
																						// Security Provider

		// Create wallets:
		walletA = new Wallet();
		walletB = new Wallet();
		Wallet coinbase = new Wallet();

		// create genesis transaction, which sends 100 NoobCoin to walletA:
		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
		genesisTransaction.generateSignature(coinbase.privateKey); // manually sign the genesis transaction
		genesisTransaction.transactionId = "0"; // manually set the transaction id
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value,
				genesisTransaction.transactionId)); // manually add the Transactions Output
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); // its important to store
																							// our first transaction in
																							// the UTXOs list.

		System.out.println("Creating and Mining Genesis block... ");
		BlockSegundo genesis = new BlockSegundo("0");
		genesis.addTransaction(genesisTransaction);
		addBlockSegundo(genesis);

		// testing
		BlockSegundo block1 = new BlockSegundo(genesis.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		addBlockSegundo(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		BlockSegundo block2 = new BlockSegundo(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlockSegundo(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		BlockSegundo block3 = new BlockSegundo(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());

		isChainValid();

		// add our blocks to the blockchain ArrayList:

		System.out.println("Trying to Mine block 1... ");
		addBlock(new Block("Hi im the first block", "0"));

		System.out.println("Trying to Mine block 2... ");
		addBlock(new Block("Yo im the second block", blockchain.get(blockchain.size() - 1).hash));

		System.out.println("Trying to Mine block 3... ");
		addBlock(new Block("Hey im the third block", blockchain.get(blockchain.size() - 1).hash));

		System.out.println("\nBlockchain is Valid: " + isChainValidNoob());

		String blockchainJson = StringUtil.getJson(blockchain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson);

	}

	public static Boolean isChainValidNoob() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');

		// loop through blockchain to check hashes:
		for (int i = 1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i - 1);
			// compare registered hash and calculated hash:
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("Current Hashes not equal");
				return false;
			}
			// compare previous hash and registered previous hash
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			// check if hash is solved
			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}

		}
		return true;
	}

	public static Boolean isChainValid() {
		// Block currentBlock;
		// Block previousBlock;
		BlockSegundo currentBlockSegundo;
		BlockSegundo previousBlockSegundo;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');

		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>(); // a temporary working
																									// list of unspent
																									// transactions at a
																									// given block
																									// state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));

		// loop through blockchain to check hashes:
		for (int i = 1; i < blockchainsegundo.size(); i++) {
			currentBlockSegundo = blockchainsegundo.get(i);
			previousBlockSegundo = blockchainsegundo.get(i - 1);
			currentBlockSegundo = blockchainsegundo.get(i);
			previousBlockSegundo = blockchainsegundo.get(i - 1);

			// compare registered hash and calculated hash:
			if (!currentBlockSegundo.hash.equals(currentBlockSegundo.calculateHash())) {
				System.out.println("Current Hashes not equal");
				return false;
			}
			// compare previous hash and registered previous hash
			if (!previousBlockSegundo.hash.equals(currentBlockSegundo.previousHash)) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			// check if hash is solved
			if (!currentBlockSegundo.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}

			// loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for (int t = 0; t < currentBlockSegundo.transactions.size(); t++) {
				Transaction currentTransaction = currentBlockSegundo.transactions.get(t);

				if (!currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false;
				}
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false;
				}

				for (TransactionInput input : currentTransaction.inputs) {
					tempOutput = tempUTXOs.get(input.transactionOutputId);

					if (tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}

					if (input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}

					tempUTXOs.remove(input.transactionOutputId);
				}

				for (TransactionOutput output : currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}

				if (currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if (currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}

			}

		}
		System.out.println("Blockchain is valid");

		return true;
	}

	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}

	public static void addBlockSegundo(BlockSegundo newBlock) {
		newBlock.mineBlock(difficulty);
		blockchainsegundo.add(newBlock);
	}
}
/*
 * public static void main(String[] args) { //add our blocks to the blockchain
 * ArrayList: Security.addProvider(new
 * org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle
 * as a Security Provider
 * 
 * //walletA = new Wallet(); //walletB = new Wallet();
 * 
 * //System.out.println("Private and public keys:");
 * //System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
 * //System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
 * 
 * createGenesis();
 * 
 * //Transaction transaction = new Transaction(walletA.publicKey,
 * walletB.publicKey, 5); //transaction.signature =
 * transaction.generateSignature(walletA.privateKey);
 * 
 * //System.out.println("Is signature verified:");
 * //System.out.println(transaction.verifiySignature());
 * 
 * }
 */

//System.out.println("Trying to Mine block 1... ");
//addBlock(new Block("Hi im the first block", "0"));
