package ma.enset.ebankbackend.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebankbackend.dtos.AccountHistoryDTO;
import ma.enset.ebankbackend.dtos.AccountOperationDTO;
import ma.enset.ebankbackend.dtos.BankAccountDTO;
import ma.enset.ebankbackend.dtos.CurrentBankAccountDTO;
import ma.enset.ebankbackend.dtos.CustomerDTO;
import ma.enset.ebankbackend.dtos.SavingBankAccountDTO;
import ma.enset.ebankbackend.entities.AccountOperation;
import ma.enset.ebankbackend.entities.BankAccount;
import ma.enset.ebankbackend.entities.CurrentAccount;
import ma.enset.ebankbackend.entities.Customer;
import ma.enset.ebankbackend.entities.SavingAccount;
import ma.enset.ebankbackend.enums.AccountStatus;
import ma.enset.ebankbackend.enums.OperationType;
import ma.enset.ebankbackend.exceptions.BalanceNotSufficientException;
import ma.enset.ebankbackend.exceptions.BankAccountNotFoundException;
import ma.enset.ebankbackend.exceptions.CustomerNotFoundException;
import ma.enset.ebankbackend.mappers.BankAccountMapperImpl;
import ma.enset.ebankbackend.repositories.AccountOperationRepository;
import ma.enset.ebankbackend.repositories.BankAccountRepository;
import ma.enset.ebankbackend.repositories.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final CustomerRepository customerRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;
    private final BankAccountMapperImpl dtoMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        currentAccount.setCustomer(customer);
        currentAccount.setStatus(AccountStatus.CREATED);
        CurrentAccount saved = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(saved);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);
        savingAccount.setStatus(AccountStatus.CREATED);
        SavingAccount saved = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(saved);
    }

    @Override
    public List<CustomerDTO> listCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(dtoMapper::fromCustomer)
                .collect(Collectors.toList());
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        if (bankAccount instanceof SavingAccount savingAccount) {
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }
        CurrentAccount currentAccount = (CurrentAccount) bankAccount;
        return dtoMapper.fromCurrentBankAccount(currentAccount);
    }

    @Override
    public void debit(String accountId, double amount, String description)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        if (bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficientException("Balance not sufficient");
        }
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("BankAccount not found"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount)
            throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource, amount, "Transfer to " + accountIdDestination);
        credit(accountIdDestination, amount, "Transfer from " + accountIdSource);
    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        return bankAccountRepository.findAll()
                .stream()
                .map(bankAccount -> {
                    if (bankAccount instanceof SavingAccount savingAccount) {
                        return (BankAccountDTO) dtoMapper.fromSavingBankAccount(savingAccount);
                    }
                    return dtoMapper.fromCurrentBankAccount((CurrentAccount) bankAccount);
                })
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Updating Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDTO> accountHistory(String accountId) {
        return accountOperationRepository.findByBankAccountId(accountId)
                .stream()
                .map(dtoMapper::fromAccountOperation)
                .collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO accountHistory(String accountId, int page, int size)
            throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAccountNotFoundException("Account not found"));
        Page<AccountOperation> accountOperations =
                accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent()
                .stream()
                .map(dtoMapper::fromAccountOperation)
                .collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        return customerRepository.findByNameContains(keyword)
                .stream()
                .map(dtoMapper::fromCustomer)
                .collect(Collectors.toList());
    }
}
