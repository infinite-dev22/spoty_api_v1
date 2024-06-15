package io.nomard.spoty_api_v1.services.implementations.dashboard;

import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import io.nomard.spoty_api_v1.models.LineChartModel;
import io.nomard.spoty_api_v1.models.ProductSalesModel;
import io.nomard.spoty_api_v1.models.StockAlertModel;
import io.nomard.spoty_api_v1.repositories.CustomerRepository;
import io.nomard.spoty_api_v1.repositories.ProductRepository;
import io.nomard.spoty_api_v1.repositories.SupplierRepository;
import io.nomard.spoty_api_v1.repositories.purchases.PurchaseMasterRepository;
import io.nomard.spoty_api_v1.repositories.sales.SaleMasterRepository;
import io.nomard.spoty_api_v1.services.auth.AuthServiceImpl;
import io.nomard.spoty_api_v1.services.interfaces.dashboard.MainDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
public class MainDashboardServiceImpl implements MainDashboardService {
    @Autowired
    private PurchaseMasterRepository purchaseMasterRepo;
    @Autowired
    private ProductRepository productRepo;
    @Autowired
    private SaleMasterRepository saleMasterRepo;
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private SupplierRepository supplierRepo;
    @Autowired
    private AuthServiceImpl authService;

    @Override
    @Cacheable("yearly_expenses")
    @Transactional(readOnly = true)
    public List<LineChartModel> getYearlyExpenses() {
        return purchaseMasterRepo.yearlyExpenses(authService.authUser().getTenant().getId());
    }

    @Override
    @Cacheable("monthly_expenses")
    @Transactional(readOnly = true)
    public List<LineChartModel> getMonthlyExpenses() {
        return purchaseMasterRepo.monthlyExpenses(authService.authUser().getTenant().getId());
    }

    @Override
    @Cacheable("weekly_expenses")
    @Transactional(readOnly = true)
    public List<LineChartModel> getWeeklyExpenses() {
        return purchaseMasterRepo.weeklyExpenses(authService.authUser().getTenant().getId());
    }

    @Override
    @Cacheable("yearly_incomes")
    @Transactional(readOnly = true)
    public List<LineChartModel> getYearlyIncomes() {
        return saleMasterRepo.yearlyIncomes(authService.authUser().getTenant().getId());
    }

    @Override
    @Cacheable("monthly_incomes")
    @Transactional(readOnly = true)
    public List<LineChartModel> getMonthlyIncomes() {
        return saleMasterRepo.monthlyIncomes(authService.authUser().getTenant().getId());
    }

    @Override
    @Cacheable("weekly_incomes")
    @Transactional(readOnly = true)
    public List<LineChartModel> getWeeklyIncomes() {
        return saleMasterRepo.weeklyIncomes(authService.authUser().getTenant().getId());
    }

    @Override
    @Cacheable("top_product_sold")
    @Transactional(readOnly = true)
    public List<ProductSalesModel> getTopProductsSold(@RequestParam(defaultValue = "10") Integer limit) {
            PageRequest pageRequest = PageRequest.of(0, limit);
            return saleMasterRepo.findTopProductsSold(authService.authUser().getTenant().getId(), pageRequest);
    }

    @Override
    @Cacheable("product_stock_alert")
    @Transactional(readOnly = true)
    public List<StockAlertModel> getProductsStockAlert() {
            return productRepo.productsStockAlert(authService.authUser().getTenant().getId());
    }

    @Override
    @Cacheable("recent_sales")
    @Transactional(readOnly = true)
    public List<SaleMaster> getRecentOrders(@RequestParam(defaultValue = "10") Integer limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return saleMasterRepo.findAllByTenantId(authService.authUser().getTenant().getId(), pageRequest).getContent();
    }

    @Override
    @Cacheable("dashboard_kpi")
    @Transactional(readOnly = true)
    public List<DashboardKPIModel> getDashboardKPI() {
        List<DashboardKPIModel> dashboardKPIModels = new ArrayList<>();
        dashboardKPIModels.add(saleMasterRepo.totalEarnings(authService.authUser().getTenant().getId()));
        dashboardKPIModels.add(purchaseMasterRepo.totalPurchases(authService.authUser().getTenant().getId()));
        dashboardKPIModels.add(productRepo.countProducts(authService.authUser().getTenant().getId()));
        dashboardKPIModels.add(customerRepo.countCustomers(authService.authUser().getTenant().getId()));
        dashboardKPIModels.add(supplierRepo.countSuppliers(authService.authUser().getTenant().getId()));
        return dashboardKPIModels;
    }
}
