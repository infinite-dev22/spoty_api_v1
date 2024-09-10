package io.nomard.spoty_api_v1.utils;

import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
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
    }

    public static class SaleCalculationService {

        private final TaxService taxService;
        private final DiscountService discountService;

        public SaleCalculationService(TaxService taxService, DiscountService discountService) {
            this.taxService = taxService;
            this.discountService = discountService;
        }

        public void calculate(SaleMaster purchaseMaster) throws NotFoundException {
            double subTotal = 0.00;
            double total = 0.00;

            // Calculate subTotal
            for (int i = 0; i < purchaseMaster.getSaleDetails().size(); i++) {
                purchaseMaster.getSaleDetails().get(i).setSale(purchaseMaster);
                subTotal += purchaseMaster.getSaleDetails().get(i).getUnitPrice() * purchaseMaster.getSaleDetails().get(i).getQuantity();
                purchaseMaster.getSaleDetails().get(i).setTotalPrice(subTotal);
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
        public static void calculate(RequisitionMaster quotation) {
            // Calculate subTotal
            for (int i = 0; i < quotation.getRequisitionDetails().size(); i++) {
                quotation.getRequisitionDetails().get(i).setRequisition(quotation);
            }
        }
    }
}
