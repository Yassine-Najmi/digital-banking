package ma.enset.ebankbackend;

import ma.enset.ebankbackend.dtos.BankAccountDTO;
import ma.enset.ebankbackend.dtos.CustomerDTO;
import ma.enset.ebankbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner start(BankAccountService bankAccountService) {
        return args -> {
            Stream.of("Hassan", "Yassine", "Aicha").forEach(name -> {
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name.toLowerCase() + "@gmail.com");
                bankAccountService.saveCustomer(customer);
            });

            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 90000, 9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 120000, 5.5, customer.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount : bankAccounts) {
                for (int i = 0; i < 10; i++) {
                    try {
                        bankAccountService.credit(bankAccount.getId(), 10000 + Math.random() * 120000, "Credit");
                        bankAccountService.debit(bankAccount.getId(), 1000 + Math.random() * 9000, "Debit");
                    } catch (Exception e) {
                        // solde insuffisant possible sur certains débits
                    }
                }
            }

            System.out.println("Service layer test OK : "
                    + bankAccountService.listCustomers().size() + " customers, "
                    + bankAccountService.bankAccountList().size() + " accounts");
        };
    }
}
