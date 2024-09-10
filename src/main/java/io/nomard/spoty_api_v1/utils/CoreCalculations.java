package io.nomard.spoty_api_v1.utils;

import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.services.interfaces.deductions.DiscountService;
import io.nomard.spoty_api_v1.services.interfaces.deductions.TaxService;

public class CoreCalculations {
    public static class PurchaseCalculationService {

        private final TaxService taxService;
        private final DiscountService discountService;

        public PurchaseCalculationService(TaxService taxService, DiscountService discountService) {
            this.taxService = taxService;
            this.discountService = discountService;
        }

        public void calculate(PurchaseMaster purchaseMaster) throws NotFoundException {
            double subTotal = 0.00;
            double total = 0.00;

            // Calculate subTotal
            for (int i = 0; i < purchaseMaster.getPurchaseDetails().size(); i++) {
                purchaseMaster.getPurchaseDetails().get(i).setPurchase(purchaseMaster);
                subTotal += purchaseMaster.getPurchaseDetails().get(i).getUnitCost() * purchaseMaster.getPurchaseDetails().get(i).getQuantity();
                purchaseMaster.getPurchaseDetails().get(i).setTotalCost(subTotal);
            }
            total += subTotal;

            // Apply tax if applicable
            if (purchaseMaster.getTax() != null) {
                double tax = Math.round(subTotal * (taxService.getById(purchaseMaster.getTax().getId()).getPercentage() / 100.0));
                purchaseMaster.setTaxAmount(tax);
                total += tax;
            }

            // Apply discount if applicable
            if (purchaseMaster.getDiscount() != null) {
                double discount = Math.round(subTotal * (discountService.getById(purchaseMaster.getDiscount().getId()).getPercentage() / 100.0));
                purchaseMaster.setDiscountAmount(discount);
                total -= discount;
            }

            // Add shipping fee
            total += purchaseMaster.getShippingFee();

            // Set the calculated values
            purchaseMaster.setSubTotal(subTotal);
            purchaseMaster.setTotal(total);
            purchaseMaster.setAmountDue(total - purchaseMaster.getAmountPaid());
        }

        public void calculate(PurchaseReturnMaster purchaseReturn) throws NotFoundException {
            double subTotal = 0.00;
            double total = 0.00;

            // Calculate subTotal
            for (int i = 0; i < purchaseReturn.getPurchaseReturnDetails().size(); i++) {
                purchaseReturn.getPurchaseReturnDetails().get(i).setPurchaseReturnMaster(purchaseReturn);
                subTotal += purchaseReturn.getPurchaseReturnDetails().get(i).getUnitCost() * purchaseReturn.getPurchaseReturnDetails().get(i).getQuantity();
                purchaseReturn.getPurchaseReturnDetails().get(i).setTotalCost(subTotal);
            }
            total += subTotal;

            // Apply tax if applicable
            if (purchaseReturn.getTax() != null) {
                double tax = Math.round(subTotal * (taxService.getById(purchaseReturn.getTax().getId()).getPercentage() / 100.0));
                purchaseReturn.setTaxAmount(tax);
                total += tax;
            }

            // Apply discount if applicable
            if (purchaseReturn.getDiscount() != null) {
                double discount = Math.round(subTotal * (discountService.getById(purchaseReturn.getDiscount().getId()).getPercentage() / 100.0));
                purchaseReturn.setDiscountAmount(discount);
                total -= discount;
            }

            // Add shipping fee
            total += purchaseReturn.getShippingFee();

            // Set the calculated values
            purchaseReturn.setSubTotal(subTotal);
            purchaseReturn.setTotal(total);
            purchaseReturn.setAmountDue(total - purchaseReturn.getAmountPaid());
        }
    }

    public static class SaleCalculationService {

        private final TaxService taxService;
        private final DiscountService discountService;

        public SaleCalculationService(TaxService taxService, DiscountService discountService) {
            this.taxService = taxService;
            this.discountService = discountService;
        }

        public void calculate(SaleMaster sale) throws NotFoundException {
            double subTotal = 0.00;
            double total = 0.00;

            // Calculate subTotal
            for (int i = 0; i < sale.getSaleDetails().size(); i++) {
                sale.getSaleDetails().get(i).setSale(sale);
                subTotal += sale.getSaleDetails().get(i).getUnitPrice() * sale.getSaleDetails().get(i).getQuantity();
                sale.getSaleDetails().get(i).setTotalPrice(subTotal);
            }
            total += subTotal;

            // Apply tax if applicable
            if (sale.getTax() != null) {
                double tax = Math.round(subTotal * (taxService.getById(sale.getTax().getId()).getPercentage() / 100.0));
                sale.setTaxAmount(tax);
                total += tax;
            }

            // Apply discount if applicable
            if (sale.getDiscount() != null) {
                double discount = Math.round(subTotal * (discountService.getById(sale.getDiscount().getId()).getPercentage() / 100.0));
                sale.setDiscountAmount(discount);
                total -= discount;
            }

            // Add shipping fee
            total += sale.getShippingFee();

            // Set the calculated values
            sale.setSubTotal(subTotal);
            sale.setTotal(total);
            sale.setAmountDue(total - sale.getAmountPaid());
        }

        public void calculate(SaleReturnMaster saleReturn) throws NotFoundException {
            double subTotal = 0.00;
            double total = 0.00;

            // Calculate subTotal
            for (int i = 0; i < saleReturn.getSaleReturnDetails().size(); i++) {
                saleReturn.getSaleReturnDetails().get(i).setSaleReturnMaster(saleReturn);
                subTotal += saleReturn.getSaleReturnDetails().get(i).getUnitPrice() * saleReturn.getSaleReturnDetails().get(i).getQuantity();
                saleReturn.getSaleReturnDetails().get(i).setTotalPrice(subTotal);
            }
            total += subTotal;

            // Apply tax if applicable
            if (saleReturn.getTax() != null) {
                double tax = Math.round(subTotal * (taxService.getById(saleReturn.getTax().getId()).getPercentage() / 100.0));
                saleReturn.setTaxAmount(tax);
                total += tax;
            }

            // Apply discount if applicable
            if (saleReturn.getDiscount() != null) {
                double discount = Math.round(subTotal * (discountService.getById(saleReturn.getDiscount().getId()).getPercentage() / 100.0));
                saleReturn.setDiscountAmount(discount);
                total -= discount;
            }

            // Add shipping fee
            total += saleReturn.getShippingFee();

            // Set the calculated values
            saleReturn.setSubTotal(subTotal);
            saleReturn.setTotal(total);
            saleReturn.setAmountDue(total - saleReturn.getAmountPaid());
        }
    }

    public static class QuotationCalculationService {

        private final TaxService taxService;
        private final DiscountService discountService;

        public QuotationCalculationService(TaxService taxService, DiscountService discountService) {
            this.taxService = taxService;
            this.discountService = discountService;
        }

        public void calculate(QuotationMaster quotation) throws NotFoundException {
            double subTotal = 0.00;
            double total = 0.00;

            // Calculate subTotal
            for (int i = 0; i < quotation.getQuotationDetails().size(); i++) {
                quotation.getQuotationDetails().get(i).setQuotation(quotation);
                subTotal += quotation.getQuotationDetails().get(i).getUnitPrice() * quotation.getQuotationDetails().get(i).getQuantity();
                quotation.getQuotationDetails().get(i).setTotalPrice(subTotal);
            }
            total += subTotal;

            // Apply tax if applicable
            if (quotation.getTax() != null) {
                double tax = Math.round(subTotal * (taxService.getById(quotation.getTax().getId()).getPercentage() / 100.0));
                quotation.setTaxAmount(tax);
                total += tax;
            }

            // Apply discount if applicable
            if (quotation.getDiscount() != null) {
                double discount = Math.round(subTotal * (discountService.getById(quotation.getDiscount().getId()).getPercentage() / 100.0));
                quotation.setDiscountAmount(discount);
                total -= discount;
            }

            // Add shipping fee
            total += quotation.getShippingFee();

            // Set the calculated values
            quotation.setSubTotal(subTotal);
            quotation.setTotal(total);
        }
    }

    public static class RequisitionCalculationService {
        public static void calculate(RequisitionMaster requisition) {
            // Calculate subTotal
            for (int i = 0; i < requisition.getRequisitionDetails().size(); i++) {
                requisition.getRequisitionDetails().get(i).setRequisition(requisition);
            }
        }
    }

    public static class StockInCalculationService {
        public static void calculate(StockInMaster stockin) {
            // Calculate subTotal
            for (int i = 0; i < stockin.getStockInDetails().size(); i++) {
                stockin.getStockInDetails().get(i).setStockIn(stockin);
            }
        }
    }

    public static class AdjustmentCalculationService {
        public static void calculate(AdjustmentMaster adjustment) {
            // Calculate subTotal
            for (int i = 0; i < adjustment.getAdjustmentDetails().size(); i++) {
                adjustment.getAdjustmentDetails().get(i).setAdjustment(adjustment);
            }
        }
    }

    public static class TransferCalculationService {
        public static void calculate(TransferMaster adjustment) {
            // Calculate subTotal
            for (int i = 0; i < adjustment.getTransferDetails().size(); i++) {
                adjustment.getTransferDetails().get(i).setTransfer(adjustment);
            }
        }
    }
}
