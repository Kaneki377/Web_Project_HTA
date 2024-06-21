var fieldProductCost;
var fieldSubtotal;
var fieldShippingCost;
var fieldTax;
var fieldTotal;

// Utility functions for number formatting and parsing
function formatNumber(number, decimals = 2, dec_point = '.', thousands_sep = ',') {
    number = (number + '').replace(/[^0-9+\-Ee.]/g, '');
    var n = !isFinite(+number) ? 0 : +number,
        prec = !isFinite(+decimals) ? 0 : Math.abs(decimals),
        sep = (typeof thousands_sep === 'undefined') ? ',' : thousands_sep,
        dec = (typeof dec_point === 'undefined') ? '.' : dec_point,
        s = '',
        toFixedFix = function(n, prec) {
            var k = Math.pow(10, prec);
            return '' + Math.round(n * k) / k;
        };
    s = (prec ? toFixedFix(n, prec) : '' + Math.round(n)).split('.');
    if (s[0].length > 3) {
        s[0] = s[0].replace(/\B(?=(?:\d{3})+(?!\d))/g, sep);
    }
    if ((s[1] || '').length < prec) {
        s[1] = s[1] || '';
        s[1] += new Array(prec - s[1].length + 1).join('0');
    }
    return s.join(dec);
}

function parseFormattedNumber(value) {
    return parseFloat(value.replace(/,/g, ''));
}

function setAndFormatNumberForField(fieldId, fieldValue) {
    var formattedValue = formatNumber(fieldValue, 2, '.', ',');
    console.log("FieldId: " + fieldId + " Value: " + formattedValue);
    $("#" + fieldId).val(formattedValue);
}

$(document).ready(function() {
    fieldProductCost = $("#productCost");
    fieldSubtotal = $("#subtotal");
    fieldShippingCost = $("#shippingCost");
    fieldTax = $("#tax");
    fieldTotal = $("#total");

    formatOrderAmounts();
    formatProductAmounts();

    $("#productList").on("change", ".quantity-input", function(e) {
        updateSubtotalWhenQuantityChanged($(this));
        updateOrderAmounts();
    });

    $("#productList").on("change", ".price-input", function(e) {
        updateSubtotalWhenPriceChanged($(this));
        updateOrderAmounts();
    });    

    $("#productList").on("change", ".cost-input, .ship-input", function(e) {
        updateOrderAmounts();
    });           
});

function updateOrderAmounts() {
    var totalCost = 0.0;

    $(".cost-input").each(function(e) {
        var costInputField = $(this);
        var rowNumber = costInputField.attr("rowNumber");
        var quantityValue = $("#quantity" + rowNumber).val();

        var productCost = parseFormattedNumber(costInputField.val());
        totalCost += productCost * parseInt(quantityValue);
    });

    setAndFormatNumberForField("productCost", totalCost);

    var orderSubtotal = 0.0;

    $(".subtotal-output").each(function(e) {
        var productSubtotal = parseFormattedNumber($(this).val());
        orderSubtotal += productSubtotal;
    });

    setAndFormatNumberForField("subtotal", orderSubtotal);

    var shippingCost = 0.0;

    $(".ship-input").each(function(e) {
        var productShip = parseFormattedNumber($(this).val());
        shippingCost += productShip;
    });

    setAndFormatNumberForField("shippingCost", shippingCost);

    var tax = parseFormattedNumber($("#tax").val());
    var orderTotal = orderSubtotal + tax + shippingCost;
    setAndFormatNumberForField("total", orderTotal);
}

function updateSubtotalWhenPriceChanged(input) {
    var priceValue = parseFormattedNumber(input.val());
    var rowNumber = input.attr("rowNumber");

    var quantityField = $("#quantity" + rowNumber);
    var quantityValue = quantityField.val();
    var newSubtotal = parseFloat(quantityValue) * priceValue;

    setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal);    
}

function updateSubtotalWhenQuantityChanged(input) {
    var quantityValue = input.val();
    var rowNumber = input.attr("rowNumber");
    var priceValue = parseFormattedNumber($("#price" + rowNumber).val());
    var newSubtotal = parseFloat(quantityValue) * priceValue;

    setAndFormatNumberForField("subtotal" + rowNumber, newSubtotal);
}

function formatProductAmounts() {
    $(".cost-input, .price-input, .subtotal-output, .ship-input").each(function() {
        var value = parseFormattedNumber($(this).val());
        setAndFormatNumberForField($(this).attr('id'), value);
    });
}

function formatOrderAmounts() {
    var fields = [fieldProductCost, fieldSubtotal, fieldShippingCost, fieldTax, fieldTotal];
    fields.forEach(function(field) {
        var value = parseFormattedNumber(field.val());
        setAndFormatNumberForField(field.attr('id'), value);
    });
}

function processFormBeforeSubmit() {
    setCountryName();

    var numberFields = [fieldProductCost, fieldSubtotal, fieldShippingCost, fieldTax, fieldTotal];
    numberFields.concat($(".cost-input, .price-input, .subtotal-output, .ship-input").toArray())
        .forEach(function(field) {
            $(field).val(parseFormattedNumber($(field).val()));
        });

    return true;
}

function setCountryName() {
    var selectedCountry = $("#country option:selected");
    var countryName = selectedCountry.text();
    $("#countryName").val(countryName);
}