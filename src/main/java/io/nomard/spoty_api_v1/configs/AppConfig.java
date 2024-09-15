package io.nomard.spoty_api_v1.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.nomard.spoty_api_v1.entities.*;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.entities.accounting.AccountTransaction;
import io.nomard.spoty_api_v1.entities.accounting.Expense;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentDetail;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentTransaction;
import io.nomard.spoty_api_v1.entities.deductions.Discount;
import io.nomard.spoty_api_v1.entities.deductions.Tax;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import io.nomard.spoty_api_v1.entities.hrm.pay_roll.Salary;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseDetail;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.entities.quotations.QuotationDetail;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionDetail;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnDetail;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnDetail;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import io.nomard.spoty_api_v1.entities.sales.SaleDetail;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.entities.sales.SaleTransaction;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInDetail;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.entities.transfers.TransferDetail;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import io.nomard.spoty_api_v1.entities.transfers.TransferTransaction;
import io.nomard.spoty_api_v1.filters.SpotyRequestFilter;
import io.nomard.spoty_api_v1.payments.FlutterWavePayments;
import io.nomard.spoty_api_v1.security.SpotyAuthEntryPoint;
import io.nomard.spoty_api_v1.services.auth.SpotyTokenService;
import io.nomard.spoty_api_v1.services.auth.SpotyUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
public class AppConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public Product product() {
        return new Product();
    }

    @Bean
    public User user() {
        return new User();
    }

    @Bean
    public Branch branch() {
        return new Branch();
    }

    @Bean
    public Brand brand() {
        return new Brand();
    }

    @Bean
    public ProductCategory productCategory() {
        return new ProductCategory();
    }

    @Bean
    public UnitOfMeasure unitOfMeasure() {
        return new UnitOfMeasure();
    }

    @Bean
    public Tenant organisation() {
        return new Tenant();
    }

    @Bean
    public UserProfile userProfile() {
        return new UserProfile();
    }

    @Bean
    public Supplier supplier() {
        return new Supplier();
    }

    @Bean
    public Customer customer() {
        return new Customer();
    }

    @Bean
    public Expense expense() {
        return new Expense();
    }

    @Bean
    public Currency currency() {
        return new Currency();
    }

    @Bean
    public AdjustmentDetail adjustmentDetail() {
        return new AdjustmentDetail();
    }

    @Bean
    public AdjustmentMaster adjustmentMaster() {
        return new AdjustmentMaster();
    }

    @Bean
    public PurchaseDetail purchaseDetail() {
        return new PurchaseDetail();
    }

    @Bean
    public PurchaseMaster purchaseMaster() {
        return new PurchaseMaster();
    }

    @Bean
    public QuotationDetail quotationDetail() {
        return new QuotationDetail();
    }

    @Bean
    public QuotationMaster quotationMaster() {
        return new QuotationMaster();
    }

    @Bean
    public RequisitionDetail requisitionDetail() {
        return new RequisitionDetail();
    }

    @Bean
    public RequisitionMaster requisitionMaster() {
        return new RequisitionMaster();
    }

    @Bean
    public PurchaseReturnDetail purchaseReturnDetail() {
        return new PurchaseReturnDetail();
    }

    @Bean
    public PurchaseReturnMaster purchaseReturnMaster() {
        return new PurchaseReturnMaster();
    }

    @Bean
    public SaleReturnDetail saleReturnDetail() {
        return new SaleReturnDetail();
    }

    @Bean
    public SaleReturnMaster saleReturnMaster() {
        return new SaleReturnMaster();
    }

    @Bean
    public SaleDetail saleDetail() {
        return new SaleDetail();
    }

    @Bean
    public SaleMaster saleMaster() {
        return new SaleMaster();
    }

    @Bean
    public StockInDetail stockInDetail() {
        return new StockInDetail();
    }

    @Bean
    public StockInMaster stockInMaster() {
        return new StockInMaster();
    }

    @Bean
    public TransferDetail transferDetail() {
        return new TransferDetail();
    }

    @Bean
    public TransferMaster transferMaster() {
        return new TransferMaster();
    }

    @Bean
    public Account account() {
        return new Account();
    }

    @Bean
    public AccountTransaction accountTransaction() {
        return new AccountTransaction();
    }

    @Bean
    public Designation designation() {
        return new Designation();
    }

    @Bean
    public Department department() {
        return new Department();
    }

    @Bean
    public Salary salary() {
        return new Salary();
    }

    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return new SpotyAuthEntryPoint();
    }

    @Bean
    public SpotyUserDetailsService spotyUserDetailsService() {
        return new SpotyUserDetailsService();
    }

    @Bean
    public SpotyTokenService spotyTokenService() {
        return new SpotyTokenService(secret);
    }

    @Bean
    public SpotyRequestFilter spotyRequestFilter() {
        return new SpotyRequestFilter();
    }

    @Bean
    public AdjustmentTransaction adjustmentTransaction() {
        return new AdjustmentTransaction();
    }

    @Bean
    public SaleTransaction saleTransaction() {
        return new SaleTransaction();
    }

    @Bean
    public TransferTransaction transferTransaction() {
        return new TransferTransaction();
    }

    @Bean
    public Role role() {
        return new Role();
    }

    @Bean
    public Permission permission() {
        return new Permission();
    }

    @Bean
    public FlutterWavePayments flutterWavePayments() {
        return new FlutterWavePayments();
    }

    @Bean
    public PaymentTransaction paymentTransaction() {
        return new PaymentTransaction();
    }

    @Bean
    public Discount discount() {
        return new Discount();
    }

    @Bean
    public Tax tax() {
        return new Tax();
    }
}
