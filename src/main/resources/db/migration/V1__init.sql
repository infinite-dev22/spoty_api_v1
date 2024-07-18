CREATE TABLE account_transactions
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    company_id       BIGINT NOT NULL,
    account_id       BIGINT NULL,
    transaction_date datetime NULL,
    credit DOUBLE NULL,
    debit DOUBLE NULL,
    amount DOUBLE NULL,
    note             VARCHAR(255) NULL,
    transaction_type VARCHAR(255) NULL,
    created_at       datetime NULL,
    created_by       BIGINT NULL,
    updated_at       datetime NULL,
    updated_by       BIGINT NULL,
    CONSTRAINT pk_account_transactions PRIMARY KEY (id)
);

CREATE TABLE accounts
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    company_id     BIGINT NOT NULL,
    account_name   VARCHAR(255) NULL,
    account_number VARCHAR(255) NULL,
    balance DOUBLE NULL,
    credit DOUBLE NULL,
    debit DOUBLE NULL,
    `description`  VARCHAR(255) NULL,
    created_at     datetime NULL,
    created_by     BIGINT NULL,
    updated_at     datetime NULL,
    updated_by     BIGINT NULL,
    CONSTRAINT pk_accounts PRIMARY KEY (id)
);

CREATE TABLE adjustment_details
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    product_id      BIGINT NOT NULL,
    adjustment_id   BIGINT NOT NULL,
    quantity        INT    NOT NULL,
    adjustment_type VARCHAR(255) NULL,
    created_at      datetime NULL,
    created_by      BIGINT NULL,
    updated_at      datetime NULL,
    updated_by      BIGINT NULL,
    CONSTRAINT pk_adjustment_details PRIMARY KEY (id)
);

CREATE TABLE adjustment_masters
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    ref        VARCHAR(255) NULL,
    branch_id  BIGINT NULL,
    company_id BIGINT NOT NULL,
    notes      VARCHAR(255) NULL,
    created_at datetime NULL,
    created_by BIGINT NULL,
    updated_at datetime NULL,
    updated_by BIGINT NULL,
    CONSTRAINT pk_adjustment_masters PRIMARY KEY (id)
);

CREATE TABLE adjustment_transactions
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    branch_id            BIGINT NOT NULL,
    company_id           BIGINT NOT NULL,
    product_id           BIGINT NOT NULL,
    adjustment_detail_id BIGINT NOT NULL,
    date                 datetime NULL,
    adjust_quantity      BIGINT NULL,
    adjustment_type      VARCHAR(255) NULL,
    created_at           datetime NULL,
    created_by           BIGINT NULL,
    updated_at           datetime NULL,
    updated_by           BIGINT NULL,
    CONSTRAINT pk_adjustment_transactions PRIMARY KEY (id)
);

CREATE TABLE beneficiary_badges
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    company_id          BIGINT NOT NULL,
    name                VARCHAR(255) NULL,
    beneficiary_type_id BIGINT NULL,
    color               VARCHAR(255) NULL,
    `description`       VARCHAR(255) NULL,
    created_at          datetime NULL,
    created_by          BIGINT NULL,
    updated_at          datetime NULL,
    updated_by          BIGINT NULL,
    CONSTRAINT pk_beneficiary_badges PRIMARY KEY (id)
);

CREATE TABLE beneficiary_badges_branches
(
    branch_id            BIGINT NOT NULL,
    employment_status_id BIGINT NOT NULL
);

CREATE TABLE beneficiary_types
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    company_id    BIGINT NOT NULL,
    name          VARCHAR(255) NULL,
    color         VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_beneficiary_types PRIMARY KEY (id)
);

CREATE TABLE beneficiary_types_branches
(
    branch_id            BIGINT NOT NULL,
    employment_status_id BIGINT NOT NULL
);

CREATE TABLE branches
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    name       VARCHAR(255) NOT NULL,
    city       VARCHAR(255) NOT NULL,
    phone      VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NULL,
    town       VARCHAR(255) NOT NULL,
    zip_code   VARCHAR(255) NULL,
    company_id BIGINT       NOT NULL,
    created_at datetime NULL,
    created_by BIGINT NULL,
    updated_at datetime NULL,
    updated_by BIGINT NULL,
    CONSTRAINT pk_branches PRIMARY KEY (id)
);

CREATE TABLE brands
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    image         VARCHAR(255) NULL,
    branch_id     BIGINT NULL,
    company_id    BIGINT       NOT NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_brands PRIMARY KEY (id)
);

CREATE TABLE currencies
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    code       VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    symbol     VARCHAR(255) NULL,
    company_id BIGINT NULL,
    created_at datetime NULL,
    created_by BIGINT NULL,
    updated_at datetime NULL,
    updated_by BIGINT NULL,
    CONSTRAINT pk_currencies PRIMARY KEY (id)
);

CREATE TABLE customers
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    name       VARCHAR(255) NOT NULL,
    code       VARCHAR(255) NULL,
    email      VARCHAR(255) NULL,
    phone      VARCHAR(255) NOT NULL,
    city       VARCHAR(255) NULL,
    address    VARCHAR(255) NULL,
    tax_number VARCHAR(255) NULL,
    branch_id  BIGINT NULL,
    company_id BIGINT NULL,
    country    VARCHAR(255) NOT NULL,
    created_at datetime NULL,
    created_by BIGINT NULL,
    updated_at datetime NULL,
    updated_by BIGINT NULL,
    CONSTRAINT pk_customers PRIMARY KEY (id)
);

CREATE TABLE designations
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    branch_id     BIGINT NOT NULL,
    company_id    BIGINT NOT NULL,
    name          VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_designations PRIMARY KEY (id)
);

CREATE TABLE discounts
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    branch_id  BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    name       VARCHAR(255) NULL,
    percentage DOUBLE NOT NULL,
    created_at datetime NULL,
    created_by BIGINT NULL,
    updated_at datetime NULL,
    updated_by BIGINT NULL,
    CONSTRAINT pk_discounts PRIMARY KEY (id)
);

CREATE TABLE employment_statuses
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NULL,
    color         VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    company_id    BIGINT NOT NULL,
    active        BIT(1) NOT NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_employment_statuses PRIMARY KEY (id)
);

CREATE TABLE employment_statuses_branches
(
    branch_id            BIGINT NOT NULL,
    employment_status_id BIGINT NOT NULL,
    CONSTRAINT pk_employment_statuses_branches PRIMARY KEY (branch_id, employment_status_id)
);

CREATE TABLE expense_categories
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    branch_id     BIGINT NULL,
    company_id    BIGINT       NOT NULL,
    `description` VARCHAR(255) NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_expense_categories PRIMARY KEY (id)
);

CREATE TABLE expenses
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    date                datetime     NOT NULL,
    ref                 VARCHAR(255) NULL,
    company_id          BIGINT       NOT NULL,
    account_id          BIGINT NULL,
    name                VARCHAR(255) NOT NULL,
    user_detail_id      BIGINT NULL,
    expense_category_id BIGINT       NOT NULL,
    branch_id           BIGINT NULL,
    note                VARCHAR(255) NULL,
    amount DOUBLE NOT NULL,
    created_at          datetime NULL,
    created_by          BIGINT NULL,
    updated_at          datetime NULL,
    updated_by          BIGINT NULL,
    CONSTRAINT pk_expenses PRIMARY KEY (id)
);

CREATE TABLE holidays
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    title         VARCHAR(255) NOT NULL,
    company_id    BIGINT       NOT NULL,
    branch_id     BIGINT NULL,
    start_date    datetime NULL,
    end_date      datetime NULL,
    `description` VARCHAR(255) NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_holidays PRIMARY KEY (id)
);

CREATE TABLE leave_types
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    company_id    BIGINT NOT NULL,
    name          VARCHAR(255) NULL,
    color         VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_leave_types PRIMARY KEY (id)
);

CREATE TABLE leave_types_branches
(
    branch_id     BIGINT NOT NULL,
    leave_type_id BIGINT NOT NULL
);

CREATE TABLE leaves
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    employee_id   BIGINT NULL,
    branch_id     BIGINT NOT NULL,
    company_id    BIGINT NOT NULL,
    `description` VARCHAR(255) NULL,
    start_date    datetime NULL,
    end_date      datetime NULL,
    duration      BIGINT NULL,
    leave_type_id BIGINT NULL,
    attachment    VARCHAR(255) NULL,
    status        CHAR   NOT NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_leaves PRIMARY KEY (id)
);

CREATE TABLE pay_slip_types
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    tenant_id     BIGINT NULL,
    name          VARCHAR(255) NULL,
    color         VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_pay_slip_types PRIMARY KEY (id)
);

CREATE TABLE pay_slip_types_branches
(
    branch_id            BIGINT NOT NULL,
    employment_status_id BIGINT NOT NULL
);

CREATE TABLE payment_transactions
(
    id                    BIGINT AUTO_INCREMENT NOT NULL,
    company_id            BIGINT NOT NULL,
    branch_id             BIGINT NULL,
    transaction_reference VARCHAR(255) NULL,
    plan_name             VARCHAR(255) NULL,
    paid_on               datetime NULL,
    amount                VARCHAR(255) NULL,
    recurring             BIT(1) NOT NULL,
    pay_method            VARCHAR(255) NULL,
    pay_source            VARCHAR(255) NULL,
    created_at            datetime NULL,
    created_by            BIGINT NULL,
    updated_at            datetime NULL,
    updated_by            BIGINT NULL,
    CONSTRAINT pk_payment_transactions PRIMARY KEY (id)
);

CREATE TABLE permissions
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    label         VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_permissions PRIMARY KEY (id)
);

CREATE TABLE product_categories
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    branch_id     BIGINT NULL,
    company_id    BIGINT       NOT NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_product_categories PRIMARY KEY (id)
);

CREATE TABLE products
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    unit_id       BIGINT       NOT NULL,
    category_id   BIGINT       NOT NULL,
    brand_id      BIGINT       NOT NULL,
    branch_id     BIGINT NULL,
    company_id    BIGINT       NOT NULL,
    barcode_type  VARCHAR(255) NULL,
    name          VARCHAR(255) NOT NULL,
    quantity      BIGINT NULL,
    cost_price DOUBLE NULL,
    sale_price DOUBLE NOT NULL,
    discount_id   BIGINT NULL,
    tax_id        BIGINT NULL,
    stock_alert   BIGINT NULL,
    serial_number VARCHAR(255) NULL,
    image         VARCHAR(255) NULL,
    created_at    datetime NULL,
    created_by    BIGINT       NOT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE purchase_details
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    cost DOUBLE NOT NULL,
    sub_total_cost DOUBLE NULL,
    purchase_id BIGINT NULL,
    product_id  BIGINT NOT NULL,
    quantity    INT    NOT NULL,
    created_at  datetime NULL,
    created_by  BIGINT NULL,
    updated_at  datetime NULL,
    updated_by  BIGINT NULL,
    CONSTRAINT pk_purchase_details PRIMARY KEY (id)
);

CREATE TABLE purchase_masters
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    ref             VARCHAR(255) NULL,
    date            datetime NOT NULL,
    supplier_id     BIGINT   NOT NULL,
    branch_id       BIGINT NULL,
    company_id      BIGINT   NOT NULL,
    tax_id          BIGINT NULL,
    discount_id     BIGINT NULL,
    shipping_fee DOUBLE NULL,
    amount_paid DOUBLE NOT NULL,
    total DOUBLE NOT NULL,
    sub_total DOUBLE NOT NULL,
    amount_due DOUBLE NOT NULL,
    purchase_status VARCHAR(255) NULL,
    payment_status  VARCHAR(255) NULL,
    notes           VARCHAR(255) NULL,
    created_at      datetime NULL,
    created_by      BIGINT NULL,
    updated_at      datetime NULL,
    updated_by      BIGINT NULL,
    CONSTRAINT pk_purchase_masters PRIMARY KEY (id)
);

CREATE TABLE purchase_return_details
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    cost DOUBLE NOT NULL,
    sub_total_cost DOUBLE NULL,
    purchase_return_id BIGINT NULL,
    product_id         BIGINT NOT NULL,
    quantity           INT    NOT NULL,
    created_at         datetime NULL,
    created_by         BIGINT NULL,
    updated_at         datetime NULL,
    updated_by         BIGINT NULL,
    CONSTRAINT pk_purchase_return_details PRIMARY KEY (id)
);

CREATE TABLE purchase_return_masters
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    ref             VARCHAR(255) NULL,
    date            datetime NOT NULL,
    supplier_id     BIGINT   NOT NULL,
    branch_id       BIGINT NULL,
    company_id      BIGINT   NOT NULL,
    tax_id          BIGINT NULL,
    discount_id     BIGINT NULL,
    shipping_fee DOUBLE NULL,
    amount_paid DOUBLE NOT NULL,
    total DOUBLE NOT NULL,
    sub_total DOUBLE NOT NULL,
    amount_due DOUBLE NOT NULL,
    purchase_status VARCHAR(255) NULL,
    payment_status  VARCHAR(255) NULL,
    notes           VARCHAR(255) NULL,
    created_at      datetime NULL,
    created_by      BIGINT NULL,
    updated_at      datetime NULL,
    updated_by      BIGINT NULL,
    CONSTRAINT pk_purchase_return_masters PRIMARY KEY (id)
);

CREATE TABLE quotation_details
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    sale_unit_id BIGINT NULL,
    product_id   BIGINT NOT NULL,
    quotation_id BIGINT NULL,
    tax_id       BIGINT NULL,
    discount_id  BIGINT NULL,
    sub_total DOUBLE NOT NULL,
    quantity     INT    NOT NULL,
    created_at   datetime NULL,
    created_by   BIGINT NULL,
    updated_at   datetime NULL,
    updated_by   BIGINT NULL,
    CONSTRAINT pk_quotation_details PRIMARY KEY (id)
);

CREATE TABLE quotation_masters
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    user_detail_id BIGINT NULL,
    ref            VARCHAR(255) NULL,
    customer_id    BIGINT NOT NULL,
    branch_id      BIGINT NOT NULL,
    company_id     BIGINT NOT NULL,
    tax_id         BIGINT NULL,
    discount_id    BIGINT NULL,
    net_tax DOUBLE NOT NULL,
    net_discount DOUBLE NOT NULL,
    shipping_fee DOUBLE NOT NULL,
    status         VARCHAR(255) NULL,
    total DOUBLE NULL,
    sub_total DOUBLE NULL,
    notes          VARCHAR(255) NULL,
    created_at     datetime NULL,
    created_by     BIGINT NULL,
    updated_at     datetime NULL,
    updated_by     BIGINT NULL,
    CONSTRAINT pk_quotation_masters PRIMARY KEY (id)
);

CREATE TABLE requisition_detatils
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    product_detail_id BIGINT NOT NULL,
    requisition_id    BIGINT NOT NULL,
    quantity          INT    NOT NULL,
    `description`     VARCHAR(255) NULL,
    created_at        datetime NULL,
    created_by        BIGINT NULL,
    updated_at        datetime NULL,
    updated_by        BIGINT NULL,
    CONSTRAINT pk_requisition_detatils PRIMARY KEY (id)
);

CREATE TABLE requisition_masters
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    ref            VARCHAR(255) NULL,
    date           datetime     NOT NULL,
    supplier_id    BIGINT       NOT NULL,
    branch_id      BIGINT       NOT NULL,
    company_id     BIGINT       NOT NULL,
    ship_via       VARCHAR(255) NULL,
    ship_method    VARCHAR(255) NULL,
    shipping_terms VARCHAR(255) NULL,
    delivery_date  datetime NULL,
    notes          VARCHAR(255) NULL,
    status         VARCHAR(255) NOT NULL,
    total_cost DOUBLE NOT NULL,
    created_at     datetime NULL,
    created_by     BIGINT NULL,
    updated_at     datetime NULL,
    updated_by     BIGINT NULL,
    CONSTRAINT pk_requisition_masters PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    label         VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    company_id    BIGINT NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE roles_permissions
(
    role_id        BIGINT NOT NULL,
    permissions_id BIGINT NOT NULL
);

CREATE TABLE salaries
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    branch_id        BIGINT NOT NULL,
    company_id       BIGINT NOT NULL,
    employee_id      BIGINT NULL,
    designation_id   BIGINT NULL,
    `period`         VARCHAR(255) NULL,
    pay_slip_type_id BIGINT NULL,
    status           CHAR   NOT NULL,
    salary           VARCHAR(255) NULL,
    net_salary       VARCHAR(255) NULL,
    created_at       datetime NULL,
    created_by       BIGINT NULL,
    updated_at       datetime NULL,
    updated_by       BIGINT NULL,
    CONSTRAINT pk_salaries PRIMARY KEY (id)
);

CREATE TABLE sale_details
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    sale_master_id BIGINT NOT NULL,
    product_id     BIGINT NOT NULL,
    sub_total_price DOUBLE NOT NULL,
    quantity       INT    NOT NULL,
    created_at     datetime NULL,
    created_by     BIGINT NULL,
    updated_at     datetime NULL,
    updated_by     BIGINT NULL,
    CONSTRAINT pk_sale_details PRIMARY KEY (id)
);

CREATE TABLE sale_masters
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    ref            VARCHAR(255) NULL,
    customer_id    BIGINT       NOT NULL,
    branch_id      BIGINT       NOT NULL,
    company_id     BIGINT       NOT NULL,
    tax_id         BIGINT NULL,
    discount_id    BIGINT NULL,
    total DOUBLE NULL,
    sub_total DOUBLE NULL,
    amount_paid DOUBLE NULL,
    amount_due DOUBLE NULL,
    change_amount DOUBLE NULL,
    shipping_fee DOUBLE NULL,
    payment_status VARCHAR(255) NOT NULL,
    sale_status    VARCHAR(255) NOT NULL,
    notes          VARCHAR(255) NULL,
    created_at     datetime NULL,
    created_by     BIGINT NULL,
    updated_at     datetime NULL,
    updated_by     BIGINT NULL,
    CONSTRAINT pk_sale_masters PRIMARY KEY (id)
);

CREATE TABLE sale_return_details
(
    id                    BIGINT AUTO_INCREMENT NOT NULL,
    sale_return_master_id BIGINT NOT NULL,
    product_id            BIGINT NOT NULL,
    sub_total_price DOUBLE NOT NULL,
    quantity              INT    NOT NULL,
    created_at            datetime NULL,
    created_by            BIGINT NULL,
    updated_at            datetime NULL,
    updated_by            BIGINT NULL,
    CONSTRAINT pk_sale_return_details PRIMARY KEY (id)
);

CREATE TABLE sale_return_masters
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    ref            VARCHAR(255) NULL,
    customer_id    BIGINT       NOT NULL,
    branch_id      BIGINT       NOT NULL,
    company_id     BIGINT       NOT NULL,
    tax_id         BIGINT NULL,
    discount_id    BIGINT NULL,
    total DOUBLE NULL,
    sub_total DOUBLE NULL,
    amount_paid DOUBLE NULL,
    amount_due DOUBLE NULL,
    change_amount DOUBLE NULL,
    shipping_fee DOUBLE NULL,
    payment_status VARCHAR(255) NOT NULL,
    sale_status    VARCHAR(255) NOT NULL,
    notes          VARCHAR(255) NULL,
    created_at     datetime NULL,
    created_by     BIGINT NULL,
    updated_at     datetime NULL,
    updated_by     BIGINT NULL,
    CONSTRAINT pk_sale_return_masters PRIMARY KEY (id)
);

CREATE TABLE sale_terms
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(255) NOT NULL,
    company_id    BIGINT       NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    active        BIT(1)       NOT NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_sale_terms PRIMARY KEY (id)
);

CREATE TABLE sale_terms_and_conditions
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    name       VARCHAR(255) NULL,
    company_id BIGINT NOT NULL,
    active     BIT(1) NOT NULL,
    created_at datetime NULL,
    created_by BIGINT NULL,
    updated_at datetime NULL,
    updated_by BIGINT NULL,
    CONSTRAINT pk_sale_terms_and_conditions PRIMARY KEY (id)
);

CREATE TABLE sale_terms_branches
(
    branch_id    BIGINT NOT NULL,
    sale_term_id BIGINT NOT NULL,
    CONSTRAINT pk_sale_terms_branches PRIMARY KEY (branch_id, sale_term_id)
);

CREATE TABLE sale_transactions
(
    id                    BIGINT AUTO_INCREMENT NOT NULL,
    branch_id             BIGINT NOT NULL,
    company_id            BIGINT NOT NULL,
    product_id            BIGINT NOT NULL,
    sale_detail_id        BIGINT NULL,
    sale_return_detail_id BIGINT NULL,
    date                  datetime NULL,
    sale_quantity         BIGINT NULL,
    created_at            datetime NULL,
    created_by            BIGINT NULL,
    updated_at            datetime NULL,
    updated_by            BIGINT NULL,
    CONSTRAINT pk_sale_transactions PRIMARY KEY (id)
);

CREATE TABLE stock_in_details
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    stock_in_id   BIGINT NOT NULL,
    product_id    BIGINT NOT NULL,
    quantity      INT    NOT NULL,
    serial_no     VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    location      VARCHAR(255) NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_stock_in_details PRIMARY KEY (id)
);

CREATE TABLE stock_in_masters
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    ref        VARCHAR(255) NULL,
    branch_id  BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    notes      VARCHAR(255) NULL,
    created_at datetime NULL,
    created_by BIGINT NULL,
    updated_at datetime NULL,
    updated_by BIGINT NULL,
    CONSTRAINT pk_stock_in_masters PRIMARY KEY (id)
);

CREATE TABLE stock_in_transactions
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    branch_id          BIGINT NOT NULL,
    company_id         BIGINT NOT NULL,
    product_id         BIGINT NOT NULL,
    stock_in_detail_id BIGINT NOT NULL,
    date               datetime NULL,
    stock_in_quantity  BIGINT NULL,
    created_at         datetime NULL,
    created_by         BIGINT NULL,
    updated_at         datetime NULL,
    updated_by         BIGINT NULL,
    CONSTRAINT pk_stock_in_transactions PRIMARY KEY (id)
);

CREATE TABLE stock_reports
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    branch_id            BIGINT NULL,
    company_id           BIGINT NOT NULL,
    product_name         VARCHAR(255) NULL,
    sale_price DOUBLE NULL,
    purchase_price DOUBLE NULL,
    purchased_quantity   INT NULL,
    sold_quantity        INT NULL,
    available_stock DOUBLE NULL,
    sale_stock_value     VARCHAR(255) NULL,
    purchase_stock_value VARCHAR(255) NULL,
    created_at           datetime NULL,
    created_by           BIGINT NULL,
    updated_at           datetime NULL,
    updated_by           BIGINT NULL,
    CONSTRAINT pk_stock_reports PRIMARY KEY (id)
);

CREATE TABLE suppliers
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    name       VARCHAR(255) NOT NULL,
    branch_id  BIGINT NULL,
    company_id BIGINT NULL,
    code       VARCHAR(255) NULL,
    email      VARCHAR(255) NULL,
    phone      VARCHAR(255) NOT NULL,
    tax_number VARCHAR(255) NULL,
    address    VARCHAR(255) NOT NULL,
    city       VARCHAR(255) NOT NULL,
    country    VARCHAR(255) NOT NULL,
    created_at datetime NULL,
    created_by BIGINT NULL,
    updated_at datetime NULL,
    updated_by BIGINT NULL,
    CONSTRAINT pk_suppliers PRIMARY KEY (id)
);

CREATE TABLE taxes
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    branch_id  BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    name       VARCHAR(255) NULL,
    percentage DOUBLE NOT NULL,
    created_at datetime NULL,
    created_by BIGINT NULL,
    updated_at datetime NULL,
    updated_by BIGINT NULL,
    CONSTRAINT pk_taxes PRIMARY KEY (id)
);

CREATE TABLE tenants
(
    id                    BIGINT AUTO_INCREMENT NOT NULL,
    name                  VARCHAR(255) NOT NULL,
    subscription_end_date datetime     NOT NULL,
    trial                 BIT(1) NULL,
    can_try               BIT(1) NULL,
    trial_end_date        datetime NULL,
    new_tenancy           BIT(1) NULL,
    created_at            datetime NULL,
    created_by            BIGINT NULL,
    updated_at            datetime NULL,
    updated_by            BIGINT NULL,
    CONSTRAINT pk_tenants PRIMARY KEY (id)
);

CREATE TABLE transfer_details
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    transfer_id   BIGINT NOT NULL,
    product_id    BIGINT NOT NULL,
    quantity      INT    NOT NULL,
    serial_no     VARCHAR(255) NULL,
    `description` VARCHAR(255) NULL,
    price DOUBLE NOT NULL,
    total DOUBLE NOT NULL,
    created_at    datetime NULL,
    created_by    BIGINT NULL,
    updated_at    datetime NULL,
    updated_by    BIGINT NULL,
    CONSTRAINT pk_transfer_details PRIMARY KEY (id)
);

CREATE TABLE transfer_masters
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    ref            VARCHAR(255) NULL,
    date           datetime     NOT NULL,
    from_branch_id BIGINT       NOT NULL,
    to_branch_id   BIGINT       NOT NULL,
    company_id     BIGINT       NOT NULL,
    shipping       VARCHAR(255) NULL,
    total DOUBLE NOT NULL,
    status         VARCHAR(255) NOT NULL,
    notes          VARCHAR(255) NULL,
    created_at     datetime NULL,
    created_by     BIGINT NULL,
    updated_at     datetime NULL,
    updated_by     BIGINT NULL,
    CONSTRAINT pk_transfer_masters PRIMARY KEY (id)
);

CREATE TABLE transfer_transactions
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    from_branch_id     BIGINT NOT NULL,
    to_branch_id       BIGINT NOT NULL,
    company_id         BIGINT NOT NULL,
    product_id         BIGINT NOT NULL,
    transfer_detail_id BIGINT NOT NULL,
    date               datetime NULL,
    transfer_quantity  BIGINT NULL,
    created_at         datetime NULL,
    created_by         BIGINT NULL,
    updated_at         datetime NULL,
    updated_by         BIGINT NULL,
    CONSTRAINT pk_transfer_transactions PRIMARY KEY (id)
);

CREATE TABLE units_of_measure
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    name         VARCHAR(255) NOT NULL,
    company_id   BIGINT       NOT NULL,
    short_name   VARCHAR(255) NULL,
    base_unit_id BIGINT NULL,
    operator     VARCHAR(255) NULL,
    operator_value DOUBLE NULL,
    created_at   datetime NULL,
    created_by   BIGINT NULL,
    updated_at   datetime NULL,
    updated_by   BIGINT NULL,
    CONSTRAINT pk_units_of_measure PRIMARY KEY (id)
);

CREATE TABLE user_profiles
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    branch_id  BIGINT NULL,
    company_id BIGINT NOT NULL,
    first_name VARCHAR(255) NULL,
    last_name  VARCHAR(255) NULL,
    other_name VARCHAR(255) NULL,
    phone      VARCHAR(255) NULL,
    avatar     VARCHAR(255) NULL,
    created_at datetime NULL,
    created_by BIGINT NULL,
    updated_at datetime NULL,
    updated_by BIGINT NULL,
    CONSTRAINT pk_user_profiles PRIMARY KEY (id)
);

CREATE TABLE users
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    user_profile_id BIGINT NOT NULL,
    organisation_id BIGINT NOT NULL,
    branch_id       BIGINT NOT NULL,
    email           VARCHAR(255) NULL,
    password        VARCHAR(255) NULL,
    role_id         BIGINT NOT NULL,
    active          BIT(1) NOT NULL,
    `locked`        BIT(1) NOT NULL,
    created_at      datetime NULL,
    created_by      BIGINT NULL,
    updated_at      datetime NULL,
    updated_by      BIGINT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE leaves
    ADD CONSTRAINT uc_leaves_employee UNIQUE (employee_id);

ALTER TABLE requisition_masters
    ADD CONSTRAINT uc_requisition_masters_supplier UNIQUE (supplier_id);

ALTER TABLE user_profiles
    ADD CONSTRAINT uc_user_profiles_avatar UNIQUE (avatar);

ALTER TABLE accounts
    ADD CONSTRAINT FK_ACCOUNTS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE accounts
    ADD CONSTRAINT FK_ACCOUNTS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE accounts
    ADD CONSTRAINT FK_ACCOUNTS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE account_transactions
    ADD CONSTRAINT FK_ACCOUNT_TRANSACTIONS_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES accounts (id);

ALTER TABLE account_transactions
    ADD CONSTRAINT FK_ACCOUNT_TRANSACTIONS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE account_transactions
    ADD CONSTRAINT FK_ACCOUNT_TRANSACTIONS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE account_transactions
    ADD CONSTRAINT FK_ACCOUNT_TRANSACTIONS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE adjustment_details
    ADD CONSTRAINT FK_ADJUSTMENT_DETAILS_ON_ADJUSTMENT FOREIGN KEY (adjustment_id) REFERENCES adjustment_masters (id);

ALTER TABLE adjustment_details
    ADD CONSTRAINT FK_ADJUSTMENT_DETAILS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE adjustment_details
    ADD CONSTRAINT FK_ADJUSTMENT_DETAILS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE adjustment_details
    ADD CONSTRAINT FK_ADJUSTMENT_DETAILS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE adjustment_masters
    ADD CONSTRAINT FK_ADJUSTMENT_MASTERS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE adjustment_masters
    ADD CONSTRAINT FK_ADJUSTMENT_MASTERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE adjustment_masters
    ADD CONSTRAINT FK_ADJUSTMENT_MASTERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE adjustment_masters
    ADD CONSTRAINT FK_ADJUSTMENT_MASTERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE adjustment_transactions
    ADD CONSTRAINT FK_ADJUSTMENT_TRANSACTIONS_ON_ADJUSTMENT_DETAIL FOREIGN KEY (adjustment_detail_id) REFERENCES adjustment_details (id);

ALTER TABLE adjustment_transactions
    ADD CONSTRAINT FK_ADJUSTMENT_TRANSACTIONS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE adjustment_transactions
    ADD CONSTRAINT FK_ADJUSTMENT_TRANSACTIONS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE adjustment_transactions
    ADD CONSTRAINT FK_ADJUSTMENT_TRANSACTIONS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE adjustment_transactions
    ADD CONSTRAINT FK_ADJUSTMENT_TRANSACTIONS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE adjustment_transactions
    ADD CONSTRAINT FK_ADJUSTMENT_TRANSACTIONS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE beneficiary_badges
    ADD CONSTRAINT FK_BENEFICIARY_BADGES_ON_BENEFICIARY_TYPE FOREIGN KEY (beneficiary_type_id) REFERENCES beneficiary_types (id);

ALTER TABLE beneficiary_badges
    ADD CONSTRAINT FK_BENEFICIARY_BADGES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE beneficiary_badges
    ADD CONSTRAINT FK_BENEFICIARY_BADGES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE beneficiary_badges
    ADD CONSTRAINT FK_BENEFICIARY_BADGES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE beneficiary_types
    ADD CONSTRAINT FK_BENEFICIARY_TYPES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE beneficiary_types
    ADD CONSTRAINT FK_BENEFICIARY_TYPES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE beneficiary_types
    ADD CONSTRAINT FK_BENEFICIARY_TYPES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE branches
    ADD CONSTRAINT FK_BRANCHES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE branches
    ADD CONSTRAINT FK_BRANCHES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE branches
    ADD CONSTRAINT FK_BRANCHES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE brands
    ADD CONSTRAINT FK_BRANDS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE brands
    ADD CONSTRAINT FK_BRANDS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE brands
    ADD CONSTRAINT FK_BRANDS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE brands
    ADD CONSTRAINT FK_BRANDS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE currencies
    ADD CONSTRAINT FK_CURRENCIES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE currencies
    ADD CONSTRAINT FK_CURRENCIES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE currencies
    ADD CONSTRAINT FK_CURRENCIES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE customers
    ADD CONSTRAINT FK_CUSTOMERS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE customers
    ADD CONSTRAINT FK_CUSTOMERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE customers
    ADD CONSTRAINT FK_CUSTOMERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE customers
    ADD CONSTRAINT FK_CUSTOMERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE designations
    ADD CONSTRAINT FK_DESIGNATIONS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE designations
    ADD CONSTRAINT FK_DESIGNATIONS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE designations
    ADD CONSTRAINT FK_DESIGNATIONS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE designations
    ADD CONSTRAINT FK_DESIGNATIONS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE discounts
    ADD CONSTRAINT FK_DISCOUNTS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE discounts
    ADD CONSTRAINT FK_DISCOUNTS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE discounts
    ADD CONSTRAINT FK_DISCOUNTS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE discounts
    ADD CONSTRAINT FK_DISCOUNTS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE employment_statuses
    ADD CONSTRAINT FK_EMPLOYMENT_STATUSES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE employment_statuses
    ADD CONSTRAINT FK_EMPLOYMENT_STATUSES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE employment_statuses
    ADD CONSTRAINT FK_EMPLOYMENT_STATUSES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_ACCOUNT FOREIGN KEY (account_id) REFERENCES accounts (id);

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_EXPENSE_CATEGORY FOREIGN KEY (expense_category_id) REFERENCES expense_categories (id);

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE expenses
    ADD CONSTRAINT FK_EXPENSES_ON_USER_DETAIL FOREIGN KEY (user_detail_id) REFERENCES users (id);

ALTER TABLE expense_categories
    ADD CONSTRAINT FK_EXPENSE_CATEGORIES_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE expense_categories
    ADD CONSTRAINT FK_EXPENSE_CATEGORIES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE expense_categories
    ADD CONSTRAINT FK_EXPENSE_CATEGORIES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE expense_categories
    ADD CONSTRAINT FK_EXPENSE_CATEGORIES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE holidays
    ADD CONSTRAINT FK_HOLIDAYS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE holidays
    ADD CONSTRAINT FK_HOLIDAYS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE holidays
    ADD CONSTRAINT FK_HOLIDAYS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE holidays
    ADD CONSTRAINT FK_HOLIDAYS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE leaves
    ADD CONSTRAINT FK_LEAVES_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE leaves
    ADD CONSTRAINT FK_LEAVES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE leaves
    ADD CONSTRAINT FK_LEAVES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE leaves
    ADD CONSTRAINT FK_LEAVES_ON_EMPLOYEE FOREIGN KEY (employee_id) REFERENCES users (id);

ALTER TABLE leaves
    ADD CONSTRAINT FK_LEAVES_ON_LEAVE_TYPE FOREIGN KEY (leave_type_id) REFERENCES leave_types (id);

ALTER TABLE leaves
    ADD CONSTRAINT FK_LEAVES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE leave_types
    ADD CONSTRAINT FK_LEAVE_TYPES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE leave_types
    ADD CONSTRAINT FK_LEAVE_TYPES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE leave_types
    ADD CONSTRAINT FK_LEAVE_TYPES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE payment_transactions
    ADD CONSTRAINT FK_PAYMENT_TRANSACTIONS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE payment_transactions
    ADD CONSTRAINT FK_PAYMENT_TRANSACTIONS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE payment_transactions
    ADD CONSTRAINT FK_PAYMENT_TRANSACTIONS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE payment_transactions
    ADD CONSTRAINT FK_PAYMENT_TRANSACTIONS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE pay_slip_types
    ADD CONSTRAINT FK_PAY_SLIP_TYPES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE pay_slip_types
    ADD CONSTRAINT FK_PAY_SLIP_TYPES_ON_TENANT FOREIGN KEY (tenant_id) REFERENCES tenants (id);

ALTER TABLE pay_slip_types
    ADD CONSTRAINT FK_PAY_SLIP_TYPES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE permissions
    ADD CONSTRAINT FK_PERMISSIONS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE permissions
    ADD CONSTRAINT FK_PERMISSIONS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_BRAND FOREIGN KEY (brand_id) REFERENCES brands (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES product_categories (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_DISCOUNT FOREIGN KEY (discount_id) REFERENCES discounts (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_TAX FOREIGN KEY (tax_id) REFERENCES taxes (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_UNIT FOREIGN KEY (unit_id) REFERENCES units_of_measure (id);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE product_categories
    ADD CONSTRAINT FK_PRODUCT_CATEGORIES_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE product_categories
    ADD CONSTRAINT FK_PRODUCT_CATEGORIES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE product_categories
    ADD CONSTRAINT FK_PRODUCT_CATEGORIES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE product_categories
    ADD CONSTRAINT FK_PRODUCT_CATEGORIES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE purchase_details
    ADD CONSTRAINT FK_PURCHASE_DETAILS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE purchase_details
    ADD CONSTRAINT FK_PURCHASE_DETAILS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE purchase_details
    ADD CONSTRAINT FK_PURCHASE_DETAILS_ON_PURCHASE FOREIGN KEY (purchase_id) REFERENCES purchase_masters (id);

ALTER TABLE purchase_details
    ADD CONSTRAINT FK_PURCHASE_DETAILS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE purchase_masters
    ADD CONSTRAINT FK_PURCHASE_MASTERS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE purchase_masters
    ADD CONSTRAINT FK_PURCHASE_MASTERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE purchase_masters
    ADD CONSTRAINT FK_PURCHASE_MASTERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE purchase_masters
    ADD CONSTRAINT FK_PURCHASE_MASTERS_ON_DISCOUNT FOREIGN KEY (discount_id) REFERENCES discounts (id);

ALTER TABLE purchase_masters
    ADD CONSTRAINT FK_PURCHASE_MASTERS_ON_SUPPLIER FOREIGN KEY (supplier_id) REFERENCES suppliers (id);

ALTER TABLE purchase_masters
    ADD CONSTRAINT FK_PURCHASE_MASTERS_ON_TAX FOREIGN KEY (tax_id) REFERENCES taxes (id);

ALTER TABLE purchase_masters
    ADD CONSTRAINT FK_PURCHASE_MASTERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE purchase_return_details
    ADD CONSTRAINT FK_PURCHASE_RETURN_DETAILS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE purchase_return_details
    ADD CONSTRAINT FK_PURCHASE_RETURN_DETAILS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE purchase_return_details
    ADD CONSTRAINT FK_PURCHASE_RETURN_DETAILS_ON_PURCHASE_RETURN FOREIGN KEY (purchase_return_id) REFERENCES purchase_return_masters (id);

ALTER TABLE purchase_return_details
    ADD CONSTRAINT FK_PURCHASE_RETURN_DETAILS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE purchase_return_masters
    ADD CONSTRAINT FK_PURCHASE_RETURN_MASTERS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE purchase_return_masters
    ADD CONSTRAINT FK_PURCHASE_RETURN_MASTERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE purchase_return_masters
    ADD CONSTRAINT FK_PURCHASE_RETURN_MASTERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE purchase_return_masters
    ADD CONSTRAINT FK_PURCHASE_RETURN_MASTERS_ON_DISCOUNT FOREIGN KEY (discount_id) REFERENCES discounts (id);

ALTER TABLE purchase_return_masters
    ADD CONSTRAINT FK_PURCHASE_RETURN_MASTERS_ON_SUPPLIER FOREIGN KEY (supplier_id) REFERENCES suppliers (id);

ALTER TABLE purchase_return_masters
    ADD CONSTRAINT FK_PURCHASE_RETURN_MASTERS_ON_TAX FOREIGN KEY (tax_id) REFERENCES taxes (id);

ALTER TABLE purchase_return_masters
    ADD CONSTRAINT FK_PURCHASE_RETURN_MASTERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE quotation_details
    ADD CONSTRAINT FK_QUOTATION_DETAILS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE quotation_details
    ADD CONSTRAINT FK_QUOTATION_DETAILS_ON_DISCOUNT FOREIGN KEY (discount_id) REFERENCES discounts (id);

ALTER TABLE quotation_details
    ADD CONSTRAINT FK_QUOTATION_DETAILS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE quotation_details
    ADD CONSTRAINT FK_QUOTATION_DETAILS_ON_QUOTATION FOREIGN KEY (quotation_id) REFERENCES quotation_masters (id);

ALTER TABLE quotation_details
    ADD CONSTRAINT FK_QUOTATION_DETAILS_ON_SALE_UNIT FOREIGN KEY (sale_unit_id) REFERENCES units_of_measure (id);

ALTER TABLE quotation_details
    ADD CONSTRAINT FK_QUOTATION_DETAILS_ON_TAX FOREIGN KEY (tax_id) REFERENCES taxes (id);

ALTER TABLE quotation_details
    ADD CONSTRAINT FK_QUOTATION_DETAILS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE quotation_masters
    ADD CONSTRAINT FK_QUOTATION_MASTERS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE quotation_masters
    ADD CONSTRAINT FK_QUOTATION_MASTERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE quotation_masters
    ADD CONSTRAINT FK_QUOTATION_MASTERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE quotation_masters
    ADD CONSTRAINT FK_QUOTATION_MASTERS_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customers (id);

ALTER TABLE quotation_masters
    ADD CONSTRAINT FK_QUOTATION_MASTERS_ON_DISCOUNT FOREIGN KEY (discount_id) REFERENCES discounts (id);

ALTER TABLE quotation_masters
    ADD CONSTRAINT FK_QUOTATION_MASTERS_ON_TAX FOREIGN KEY (tax_id) REFERENCES taxes (id);

ALTER TABLE quotation_masters
    ADD CONSTRAINT FK_QUOTATION_MASTERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE quotation_masters
    ADD CONSTRAINT FK_QUOTATION_MASTERS_ON_USER_DETAIL FOREIGN KEY (user_detail_id) REFERENCES users (id);

ALTER TABLE requisition_detatils
    ADD CONSTRAINT FK_REQUISITION_DETATILS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE requisition_detatils
    ADD CONSTRAINT FK_REQUISITION_DETATILS_ON_PRODUCT_DETAIL FOREIGN KEY (product_detail_id) REFERENCES products (id);

ALTER TABLE requisition_detatils
    ADD CONSTRAINT FK_REQUISITION_DETATILS_ON_REQUISITION FOREIGN KEY (requisition_id) REFERENCES requisition_masters (id);

ALTER TABLE requisition_detatils
    ADD CONSTRAINT FK_REQUISITION_DETATILS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE requisition_masters
    ADD CONSTRAINT FK_REQUISITION_MASTERS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE requisition_masters
    ADD CONSTRAINT FK_REQUISITION_MASTERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE requisition_masters
    ADD CONSTRAINT FK_REQUISITION_MASTERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE requisition_masters
    ADD CONSTRAINT FK_REQUISITION_MASTERS_ON_SUPPLIER FOREIGN KEY (supplier_id) REFERENCES suppliers (id);

ALTER TABLE requisition_masters
    ADD CONSTRAINT FK_REQUISITION_MASTERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE roles
    ADD CONSTRAINT FK_ROLES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE roles
    ADD CONSTRAINT FK_ROLES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE roles
    ADD CONSTRAINT FK_ROLES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE salaries
    ADD CONSTRAINT FK_SALARIES_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE salaries
    ADD CONSTRAINT FK_SALARIES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE salaries
    ADD CONSTRAINT FK_SALARIES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE salaries
    ADD CONSTRAINT FK_SALARIES_ON_DESIGNATION FOREIGN KEY (designation_id) REFERENCES designations (id);

ALTER TABLE salaries
    ADD CONSTRAINT FK_SALARIES_ON_EMPLOYEE FOREIGN KEY (employee_id) REFERENCES users (id);

ALTER TABLE salaries
    ADD CONSTRAINT FK_SALARIES_ON_PAY_SLIP_TYPE FOREIGN KEY (pay_slip_type_id) REFERENCES pay_slip_types (id);

ALTER TABLE salaries
    ADD CONSTRAINT FK_SALARIES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE sale_details
    ADD CONSTRAINT FK_SALE_DETAILS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE sale_details
    ADD CONSTRAINT FK_SALE_DETAILS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE sale_details
    ADD CONSTRAINT FK_SALE_DETAILS_ON_SALE_MASTER FOREIGN KEY (sale_master_id) REFERENCES sale_masters (id);

ALTER TABLE sale_details
    ADD CONSTRAINT FK_SALE_DETAILS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE sale_masters
    ADD CONSTRAINT FK_SALE_MASTERS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE sale_masters
    ADD CONSTRAINT FK_SALE_MASTERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE sale_masters
    ADD CONSTRAINT FK_SALE_MASTERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE sale_masters
    ADD CONSTRAINT FK_SALE_MASTERS_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customers (id);

ALTER TABLE sale_masters
    ADD CONSTRAINT FK_SALE_MASTERS_ON_DISCOUNT FOREIGN KEY (discount_id) REFERENCES discounts (id);

ALTER TABLE sale_masters
    ADD CONSTRAINT FK_SALE_MASTERS_ON_TAX FOREIGN KEY (tax_id) REFERENCES taxes (id);

ALTER TABLE sale_masters
    ADD CONSTRAINT FK_SALE_MASTERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE sale_return_details
    ADD CONSTRAINT FK_SALE_RETURN_DETAILS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE sale_return_details
    ADD CONSTRAINT FK_SALE_RETURN_DETAILS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE sale_return_details
    ADD CONSTRAINT FK_SALE_RETURN_DETAILS_ON_SALE_RETURN_MASTER FOREIGN KEY (sale_return_master_id) REFERENCES sale_return_masters (id);

ALTER TABLE sale_return_details
    ADD CONSTRAINT FK_SALE_RETURN_DETAILS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE sale_return_masters
    ADD CONSTRAINT FK_SALE_RETURN_MASTERS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE sale_return_masters
    ADD CONSTRAINT FK_SALE_RETURN_MASTERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE sale_return_masters
    ADD CONSTRAINT FK_SALE_RETURN_MASTERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE sale_return_masters
    ADD CONSTRAINT FK_SALE_RETURN_MASTERS_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customers (id);

ALTER TABLE sale_return_masters
    ADD CONSTRAINT FK_SALE_RETURN_MASTERS_ON_DISCOUNT FOREIGN KEY (discount_id) REFERENCES discounts (id);

ALTER TABLE sale_return_masters
    ADD CONSTRAINT FK_SALE_RETURN_MASTERS_ON_TAX FOREIGN KEY (tax_id) REFERENCES taxes (id);

ALTER TABLE sale_return_masters
    ADD CONSTRAINT FK_SALE_RETURN_MASTERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE sale_terms_and_conditions
    ADD CONSTRAINT FK_SALE_TERMS_AND_CONDITIONS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE sale_terms_and_conditions
    ADD CONSTRAINT FK_SALE_TERMS_AND_CONDITIONS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE sale_terms_and_conditions
    ADD CONSTRAINT FK_SALE_TERMS_AND_CONDITIONS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE sale_terms
    ADD CONSTRAINT FK_SALE_TERMS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE sale_terms
    ADD CONSTRAINT FK_SALE_TERMS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE sale_terms
    ADD CONSTRAINT FK_SALE_TERMS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE sale_transactions
    ADD CONSTRAINT FK_SALE_TRANSACTIONS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE sale_transactions
    ADD CONSTRAINT FK_SALE_TRANSACTIONS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE sale_transactions
    ADD CONSTRAINT FK_SALE_TRANSACTIONS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE sale_transactions
    ADD CONSTRAINT FK_SALE_TRANSACTIONS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE sale_transactions
    ADD CONSTRAINT FK_SALE_TRANSACTIONS_ON_SALE_DETAIL FOREIGN KEY (sale_detail_id) REFERENCES sale_details (id);

ALTER TABLE sale_transactions
    ADD CONSTRAINT FK_SALE_TRANSACTIONS_ON_SALE_RETURN_DETAIL FOREIGN KEY (sale_return_detail_id) REFERENCES sale_return_details (id);

ALTER TABLE sale_transactions
    ADD CONSTRAINT FK_SALE_TRANSACTIONS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE stock_in_details
    ADD CONSTRAINT FK_STOCK_IN_DETAILS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE stock_in_details
    ADD CONSTRAINT FK_STOCK_IN_DETAILS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE stock_in_details
    ADD CONSTRAINT FK_STOCK_IN_DETAILS_ON_STOCK_IN FOREIGN KEY (stock_in_id) REFERENCES stock_in_masters (id);

ALTER TABLE stock_in_details
    ADD CONSTRAINT FK_STOCK_IN_DETAILS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE stock_in_masters
    ADD CONSTRAINT FK_STOCK_IN_MASTERS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE stock_in_masters
    ADD CONSTRAINT FK_STOCK_IN_MASTERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE stock_in_masters
    ADD CONSTRAINT FK_STOCK_IN_MASTERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE stock_in_masters
    ADD CONSTRAINT FK_STOCK_IN_MASTERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE stock_in_transactions
    ADD CONSTRAINT FK_STOCK_IN_TRANSACTIONS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE stock_in_transactions
    ADD CONSTRAINT FK_STOCK_IN_TRANSACTIONS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE stock_in_transactions
    ADD CONSTRAINT FK_STOCK_IN_TRANSACTIONS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE stock_in_transactions
    ADD CONSTRAINT FK_STOCK_IN_TRANSACTIONS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE stock_in_transactions
    ADD CONSTRAINT FK_STOCK_IN_TRANSACTIONS_ON_STOCK_IN_DETAIL FOREIGN KEY (stock_in_detail_id) REFERENCES stock_in_details (id);

ALTER TABLE stock_in_transactions
    ADD CONSTRAINT FK_STOCK_IN_TRANSACTIONS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE stock_reports
    ADD CONSTRAINT FK_STOCK_REPORTS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE stock_reports
    ADD CONSTRAINT FK_STOCK_REPORTS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE stock_reports
    ADD CONSTRAINT FK_STOCK_REPORTS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE stock_reports
    ADD CONSTRAINT FK_STOCK_REPORTS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE suppliers
    ADD CONSTRAINT FK_SUPPLIERS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE suppliers
    ADD CONSTRAINT FK_SUPPLIERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE suppliers
    ADD CONSTRAINT FK_SUPPLIERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE suppliers
    ADD CONSTRAINT FK_SUPPLIERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE taxes
    ADD CONSTRAINT FK_TAXES_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE taxes
    ADD CONSTRAINT FK_TAXES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE taxes
    ADD CONSTRAINT FK_TAXES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE taxes
    ADD CONSTRAINT FK_TAXES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE tenants
    ADD CONSTRAINT FK_TENANTS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE tenants
    ADD CONSTRAINT FK_TENANTS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE transfer_details
    ADD CONSTRAINT FK_TRANSFER_DETAILS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE transfer_details
    ADD CONSTRAINT FK_TRANSFER_DETAILS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE transfer_details
    ADD CONSTRAINT FK_TRANSFER_DETAILS_ON_TRANSFER FOREIGN KEY (transfer_id) REFERENCES transfer_masters (id);

ALTER TABLE transfer_details
    ADD CONSTRAINT FK_TRANSFER_DETAILS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE transfer_masters
    ADD CONSTRAINT FK_TRANSFER_MASTERS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE transfer_masters
    ADD CONSTRAINT FK_TRANSFER_MASTERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE transfer_masters
    ADD CONSTRAINT FK_TRANSFER_MASTERS_ON_FROM_BRANCH FOREIGN KEY (from_branch_id) REFERENCES branches (id);

ALTER TABLE transfer_masters
    ADD CONSTRAINT FK_TRANSFER_MASTERS_ON_TO_BRANCH FOREIGN KEY (to_branch_id) REFERENCES branches (id);

ALTER TABLE transfer_masters
    ADD CONSTRAINT FK_TRANSFER_MASTERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE transfer_transactions
    ADD CONSTRAINT FK_TRANSFER_TRANSACTIONS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE transfer_transactions
    ADD CONSTRAINT FK_TRANSFER_TRANSACTIONS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE transfer_transactions
    ADD CONSTRAINT FK_TRANSFER_TRANSACTIONS_ON_FROM_BRANCH FOREIGN KEY (from_branch_id) REFERENCES branches (id);

ALTER TABLE transfer_transactions
    ADD CONSTRAINT FK_TRANSFER_TRANSACTIONS_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES products (id);

ALTER TABLE transfer_transactions
    ADD CONSTRAINT FK_TRANSFER_TRANSACTIONS_ON_TO_BRANCH FOREIGN KEY (to_branch_id) REFERENCES branches (id);

ALTER TABLE transfer_transactions
    ADD CONSTRAINT FK_TRANSFER_TRANSACTIONS_ON_TRANSFER_DETAIL FOREIGN KEY (transfer_detail_id) REFERENCES transfer_details (id);

ALTER TABLE transfer_transactions
    ADD CONSTRAINT FK_TRANSFER_TRANSACTIONS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE units_of_measure
    ADD CONSTRAINT FK_UNITS_OF_MEASURE_ON_BASE_UNIT FOREIGN KEY (base_unit_id) REFERENCES units_of_measure (id);

ALTER TABLE units_of_measure
    ADD CONSTRAINT FK_UNITS_OF_MEASURE_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE units_of_measure
    ADD CONSTRAINT FK_UNITS_OF_MEASURE_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE units_of_measure
    ADD CONSTRAINT FK_UNITS_OF_MEASURE_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ORGANISATION FOREIGN KEY (organisation_id) REFERENCES tenants (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_USERPROFILE FOREIGN KEY (user_profile_id) REFERENCES user_profiles (id);

ALTER TABLE user_profiles
    ADD CONSTRAINT FK_USER_PROFILES_ON_BRANCH FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE user_profiles
    ADD CONSTRAINT FK_USER_PROFILES_ON_COMPANY FOREIGN KEY (company_id) REFERENCES tenants (id);

ALTER TABLE user_profiles
    ADD CONSTRAINT FK_USER_PROFILES_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE user_profiles
    ADD CONSTRAINT FK_USER_PROFILES_ON_UPDATED_BY FOREIGN KEY (updated_by) REFERENCES users (id);

ALTER TABLE beneficiary_badges_branches
    ADD CONSTRAINT fk_benbadbra_on_beneficiary_badge FOREIGN KEY (employment_status_id) REFERENCES beneficiary_badges (id);

ALTER TABLE beneficiary_badges_branches
    ADD CONSTRAINT fk_benbadbra_on_branch FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE beneficiary_types_branches
    ADD CONSTRAINT fk_bentypbra_on_beneficiary_type FOREIGN KEY (employment_status_id) REFERENCES beneficiary_types (id);

ALTER TABLE beneficiary_types_branches
    ADD CONSTRAINT fk_bentypbra_on_branch FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE employment_statuses_branches
    ADD CONSTRAINT fk_empstabra_on_branch FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE employment_statuses_branches
    ADD CONSTRAINT fk_empstabra_on_employment_status FOREIGN KEY (employment_status_id) REFERENCES employment_statuses (id);

ALTER TABLE leave_types_branches
    ADD CONSTRAINT fk_leatypbra_on_branch FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE leave_types_branches
    ADD CONSTRAINT fk_leatypbra_on_leave_type FOREIGN KEY (leave_type_id) REFERENCES leave_types (id);

ALTER TABLE pay_slip_types_branches
    ADD CONSTRAINT fk_payslitypbra_on_branch FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE pay_slip_types_branches
    ADD CONSTRAINT fk_payslitypbra_on_pay_slip_type FOREIGN KEY (employment_status_id) REFERENCES pay_slip_types (id);

ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rolper_on_permission FOREIGN KEY (permissions_id) REFERENCES permissions (id);

ALTER TABLE roles_permissions
    ADD CONSTRAINT fk_rolper_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE sale_terms_branches
    ADD CONSTRAINT fk_salterbra_on_branch FOREIGN KEY (branch_id) REFERENCES branches (id);

ALTER TABLE sale_terms_branches
    ADD CONSTRAINT fk_salterbra_on_sale_term FOREIGN KEY (sale_term_id) REFERENCES sale_terms (id);