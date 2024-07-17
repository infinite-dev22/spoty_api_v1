package io.nomard.spoty_api_v1.services.implementations.dashboard;

import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.LineChartModel;
import io.nomard.spoty_api_v1.models.ProductSalesModel;
import io.nomard.spoty_api_v1.models.StockAlertModel;
import io.nomard.spoty_api_v1.repositories.CustomerRepository;
import io.nomard.spoty_api_v1.repositories.ProductRepository;
import io.nomard.spoty_api_v1.repositories.SupplierRepository;
import io.nomard.spoty_api_v1.repositories.purchases.PurchaseRepository;
import io.nomard.spoty_api_v1.repositories.sales.SaleRepository;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.dashboard.MainDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MainDashboardServiceImpl implements MainDashboardService {
    @Autowired
    private PurchaseRepository purchaseMasterRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private SaleRepository saleMasterRepo;
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private SupplierRepository supplierRepo;
    @Autowired
    private AuthServiceImpl authService;

    @Override
    @Cacheable("yearly_expenses")
    @Transactional(readOnly = true)
    public Flux<LineChartModel> getYearlyExpenses() {
        return authService.authUser()
                .flatMapMany(user -> purchaseMasterRepo.yearlyExpenses(user.getTenant().getId()));
    }

    @Override
    @Cacheable("monthly_expenses")
    @Transactional(readOnly = true)
    public Flux<LineChartModel> getMonthlyExpenses() {
        return authService.authUser()
                .flatMapMany(user -> purchaseMasterRepo.monthlyExpenses(user.getTenant().getId()));
    }

    @Override
    @Cacheable("weekly_expenses")
    @Transactional(readOnly = true)
    public Flux<LineChartModel> getWeeklyExpenses() {
        return authService.authUser()
                .flatMapMany(user -> purchaseMasterRepo.weeklyExpenses(user.getTenant().getId()));
    }

    @Override
    @Cacheable("yearly_incomes")
    @Transactional(readOnly = true)
    public Flux<LineChartModel> getYearlyIncomes() {
        return authService.authUser()
                .flatMapMany(user -> saleMasterRepo.yearlyIncomes(user.getTenant().getId()));
    }

    @Override
    @Cacheable("monthly_incomes")
    @Transactional(readOnly = true)
    public Flux<LineChartModel> getMonthlyIncomes() {
        return authService.authUser()
                .flatMapMany(user -> saleMasterRepo.monthlyIncomes(user.getTenant().getId()));
    }

    @Override
    @Cacheable("monthly_revenue")
    @Transactional(readOnly = true)
    public Flux<LineChartModel> getMonthlyRevenue() {
        return authService.authUser()
                .flatMapMany(user -> saleMasterRepo.monthlyRevenue(user.getTenant().getId()));
    }

    @Override
    @Cacheable("weekly_revenue")
    @Transactional(readOnly = true)
    public Flux<LineChartModel> getWeeklyRevenue() {
        return authService.authUser()
                .flatMapMany(user -> saleMasterRepo.weeklyRevenue(user.getTenant().getId()));
    }

    @Override
    @Cacheable("top_product_sold")
    @Transactional(readOnly = true)
    public Flux<ProductSalesModel> getTopProductsSold(@RequestParam(defaultValue = "10") Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        return authService.authUser()
                .flatMapMany(user -> saleMasterRepo.findTopProductsSold(user.getId(), pageRequest));
    }

    @Override
    @Cacheable("product_stock_alert")
    @Transactional(readOnly = true)
    public Flux<StockAlertModel> getProductsStockAlert() {
        return authService.authUser()
                .flatMapMany(user -> productRepo.productsStockAlert(user.getTenant().getId()));
    }

    @Override
    @Cacheable("recent_sales")
    @Transactional(readOnly = true)
    public Flux<SaleMaster> getRecentOrders(@RequestParam(defaultValue = "10") Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return authService.authUser()
                .flatMapMany(user -> saleMasterRepo.findAllByTenantId(user.getTenant().getId(), pageRequest));
    }

    @Override
    @Cacheable("total_earnings")
    @Transactional(readOnly = true)
    public Mono<DashboardKPIModel> getTotalEarningsKPI() {
        return authService.authUser()
                .flatMap(user -> saleMasterRepo.totalEarnings(user.getTenant().getId()));
    }

    @Override
    @Cacheable("total_purchases")
    @Transactional(readOnly = true)
    public Mono<DashboardKPIModel> getTotalPurchasesKPI() {
        return authService.authUser()
                .flatMap(user -> purchaseMasterRepo.totalPurchases(user.getTenant().getId()));
    }

    @Override
    @Cacheable("total_products")
    @Transactional(readOnly = true)
    public Mono<DashboardKPIModel> getCountProductsKPI() {
        return authService.authUser()
                .flatMap(user -> productRepo.countProducts(user.getTenant().getId()));
    }

    @Override
    @Cacheable("total_customers")
    @Transactional(readOnly = true)
    public Mono<DashboardKPIModel> getCountCustomersKPI() {
        return authService.authUser()
                .flatMap(user -> customerRepo.countCustomers(user.getTenant().getId()));
    }

    @Override
    @Cacheable("total_suppliers")
    @Transactional(readOnly = true)
    public Mono<DashboardKPIModel> getCountSuppliersKPI() {
        return authService.authUser()
                .flatMap(user -> supplierRepo.countSuppliers(user.getTenant().getId()));
    }
}
