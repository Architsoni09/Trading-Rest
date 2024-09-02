package com.trading.tradingbackend.Service;

import com.trading.tradingbackend.Enums.WALLET_TRANSACTION_TYPE;
import com.trading.tradingbackend.Enums.WITHDRAWAL_STATUS;
import com.trading.tradingbackend.Model.User;
import com.trading.tradingbackend.Model.Wallet;
import com.trading.tradingbackend.Model.WalletTransaction;
import com.trading.tradingbackend.Model.Withdrawal;
import com.trading.tradingbackend.Repository.WalletTransactionRepository;
import com.trading.tradingbackend.Repository.WithdrawalRepository;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WithdrawalService {
    private final WithdrawalRepository withdrawalRepository;
    private final WalletService walletService;
    private final WalletTransactionRepository walletTransactionRepository;

    public Withdrawal requestWithdrawal(Long amount, User user){
        Withdrawal withdrawal=new Withdrawal();
        withdrawal.setAmount(amount);
        withdrawal.setUser(user);
        withdrawal.setWithdrawalStatus(WITHDRAWAL_STATUS.PENDING);
        return withdrawalRepository.save(withdrawal);
    }

    public Withdrawal proceedWithWithdrawal(Long withdrawalId,boolean accept) throws Exception {
        Optional<Withdrawal> withdrawal=withdrawalRepository.findById(withdrawalId);
        if(withdrawal.isEmpty()) throw new Exception("Withdrawal not found");
        Withdrawal withdrawal1=withdrawal.get();
        withdrawal1.setDateTime(LocalDateTime.now());
        if(accept){
            withdrawal1.setWithdrawalStatus(WITHDRAWAL_STATUS.SUCCESS);
            Optional<User> user=withdrawalRepository.findUserByWithdrawalId(withdrawalId);
            if(user.isPresent()){
                Wallet wallet= walletService.getUserWallet(user.get());
                WalletTransaction transaction = WalletTransaction.builder()
                        .wallet(wallet)
                        .receiverWalletId(wallet.getId())
                        .transactionType(WALLET_TRANSACTION_TYPE.ADD_MONEY)
                        .date(LocalDate.now())
                        .purpose("Withdraw To Bank Account")
                        .amount(withdrawal1.getAmount())
                        .build();
                walletTransactionRepository.save(transaction);
            }
        }
        else{
            withdrawal1.setWithdrawalStatus(WITHDRAWAL_STATUS.DECLINED);
        }
        return withdrawalRepository.save(withdrawal1);
    }

    public List<Withdrawal> getUsersWithdrawalHistory(User user){
        return withdrawalRepository.findAllByUserId(user.getId());
    }

    public List<Withdrawal> getAllWithdrawalRequests(){
        return withdrawalRepository.findAll();
    }
}
