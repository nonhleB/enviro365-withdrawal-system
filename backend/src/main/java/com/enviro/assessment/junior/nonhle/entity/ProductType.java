package com.enviro.assessment.junior.nonhle.entity;

/**
 * Types of investment products Enviro365 offers.
 *
 * RETIREMENT_ANNUITY is significant because of the business rule:
 * retirement withdrawals are only allowed once the investor is older than 65.
 */
public enum ProductType {
    RETIREMENT_ANNUITY,
    UNIT_TRUST,
    SAVINGS_PLAN,
    ENDOWMENT
}
