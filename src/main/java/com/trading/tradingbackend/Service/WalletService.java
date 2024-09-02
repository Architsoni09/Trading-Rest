package com.trading.tradingbackend.Service;

import com.trading.tradingbackend.Enums.ORDER_TYPE;
import com.trading.tradingbackend.Enums.WALLET_TRANSACTION_TYPE;
import com.trading.tradingbackend.Model.Order;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Model.Wallet;
import com.trading.tradingbackend.Model.WalletTransaction;
import com.trading.tradingbackend.Repository.WalletRepository;
import com.trading.tradingbackend.Repository.WalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;

    public Wallet getUserWallet(User user) {
        Optional<Wallet> wallet=walletRepository.findByUserId(user.getId());
        if (wallet.isPresent()){
            return wallet.get();
        }
        else{
            Wallet wallet1=Wallet.builder().balance(BigDecimal.ZERO).user(user).build();
            walletRepository.save(wallet1);
            return wallet1;
        }
    }

    ;

    public Wallet addBalance(Wallet wallet, Long money,String purpose) throws Exception {
        Optional<Wallet> existingWallet = walletRepository.findById(wallet.getId());
        if (existingWallet.isPresent()) {
            existingWallet.get().setBalance(existingWallet.get().getBalance().add(BigDecimal.valueOf(Math.floor(money))));
            if(!purpose.equals("Withdrawal Pending")) {
                WalletTransaction transaction = WalletTransaction.builder()
                        .wallet(wallet)
                        .receiverWalletId(wallet.getId())
                        .transactionType(WALLET_TRANSACTION_TYPE.ADD_MONEY)
                        .date(LocalDate.now())
                        .purpose(purpose)
                        .amount(money)
                        .build();
                walletTransactionRepository.save(transaction);
            }
            return walletRepository.save(existingWallet.get());
        } else {
            throw new Exception("Could not find wallet with id " + wallet.getId());
        }
    }

    public Wallet findWalletById(Long id) {
        return walletRepository.findById(id).orElseThrow(() -> new RuntimeException("No wallet found for the given id"));
    }

    public Wallet walletToWalletTransfer(User sender, Wallet recieverWallet, Long amount,String purpose) {
        Optional<Wallet> senderWallet = walletRepository.findByUserId(sender.getId());
        Optional<Wallet> recieverOptional = walletRepository.findById(recieverWallet.getId());
        if (senderWallet.isPresent() && recieverOptional.isPresent()) {
            if (senderWallet.get().getBalance().compareTo(BigDecimal.valueOf(amount)) >= 0) {
                senderWallet.get().setBalance(BigDecimal.valueOf(Math.floor(senderWallet.get().getBalance().subtract(BigDecimal.valueOf(amount)).doubleValue())));
                recieverOptional.get().setBalance(BigDecimal.valueOf(Math.floor(recieverOptional.get().getBalance().add(BigDecimal.valueOf(amount)).doubleValue())));
                WalletTransaction senderTransaction=WalletTransaction.builder()
                        .wallet(senderWallet.get())
                        .receiverWalletId(recieverOptional.get().getId())
                        .transactionType(WALLET_TRANSACTION_TYPE.WALLET_TRANSFER)
                        .date(LocalDate.now())
                        .purpose(purpose)
                        .amount(amount)
                        .build();
                walletTransactionRepository.save(senderTransaction);
                walletRepository.save(senderWallet.get());
                WalletTransaction receiverTransaction=WalletTransaction.builder()
                        .wallet(recieverOptional.get())
                        .receiverWalletId(senderWallet.get().getId())
                        .transactionType(WALLET_TRANSACTION_TYPE.ADD_MONEY)
                        .date(LocalDate.now())
                        .purpose(purpose)
                        .amount(amount)
                        .build();
                walletTransactionRepository.save(receiverTransaction);
                walletRepository.save(recieverOptional.get());
                return senderWallet.get();
            } else {
                throw new RuntimeException("Insufficient balance in sender's wallet");
            }
        } else {
            throw new RuntimeException("Could not find sender or reciever wallet");
        }
    }

    @Transactional
    public Wallet orderFinances(Order order, User user) throws Exception {
        Wallet userWallet = getUserWallet(user);

        BigDecimal newBalance;
        if (order.getOrderType().equals(ORDER_TYPE.BUY)) {
            newBalance = BigDecimal.valueOf(Math.floor(userWallet.getBalance().subtract(order.getPrice()).doubleValue()));
            WalletTransaction senderTransaction=WalletTransaction.builder()
                    .wallet(userWallet)
                    .receiverWalletId(userWallet.getId())
                    .transactionType(WALLET_TRANSACTION_TYPE.WALLET_TRANSFER)
                    .date(LocalDate.now())
                    .purpose("Asset Purchase")
                    .amount( order.getOrderItem().getCostPrice().longValue())
                    .build();
            walletTransactionRepository.save(senderTransaction);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("Insufficient balance in user's wallet");
            }
        } else { // Assuming it's a SELL operation
            newBalance = BigDecimal.valueOf(Math.floor(userWallet.getBalance().add(order.getPrice()).doubleValue()));
            WalletTransaction senderTransaction=WalletTransaction.builder()
                    .wallet(userWallet)
                    .receiverWalletId(userWallet.getId())
                    .transactionType(WALLET_TRANSACTION_TYPE.WALLET_TRANSFER)
                    .date(LocalDate.now())
                    .purpose("Asset Sale")
                    .amount(order.getOrderItem().getSellingPrice().longValue())
                    .build();
            walletTransactionRepository.save(senderTransaction);
        }
        userWallet.setBalance(newBalance);
        return walletRepository.save(userWallet);
    }

    public List<WalletTransaction> getTransactions(User user) {
        return walletTransactionRepository.findTransactionsByUserId(user.getId()).orElse(null);
    }
}
