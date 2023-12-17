package io.nomard.spoty_api_v1.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nomard.spoty_api_v1.entities.*;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentDetail;
import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
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
import io.nomard.spoty_api_v1.entities.stock_ins.StockInDetail;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.entities.transfers.TransferDetail;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import io.nomard.spoty_api_v1.filters.SpotyRequestFilter;
import io.nomard.spoty_api_v1.security.SpotyAuthEntryPoint;
import io.nomard.spoty_api_v1.services.auth.SpotyTokenService;
import io.nomard.spoty_api_v1.services.auth.SpotyUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class AppConfig {
    @Value("jwt.secret")
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
    public Organisation organisation() {
        return new Organisation();
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
    public ExpenseCategory expenseCategory() {
        return new ExpenseCategory();
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
    public Attendance attendance() {
        return new Attendance();
    }

    @Bean
    public Bank bank() {
        return new Bank();
    }

    @Bean
    public Designation designation() {
        return new Designation();
    }

    @Bean
    public SalaryAdvance salaryAdvance() {
        return new SalaryAdvance();
    }

    @Bean
    public Salary salary() {
        return new Salary();
    }

    @Bean
    public SaleTermAndCondition saleTermAndCondition() {
        return new SaleTermAndCondition();
    }

    @Bean
    public ServiceInvoice serviceInvoice() {
        return new ServiceInvoice();
    }

    @Bean
    public ZenService service() {
        return new ZenService();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return new SpotyAuthEntryPoint();
    }

    @Bean
    public UserDetailsService userDetailsService() {
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
}
