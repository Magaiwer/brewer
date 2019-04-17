var Brewer = Brewer || {};

Brewer.MaskMoney = (function () {

    function MaskMoney() {
        this.decimal = $('.js-decimal');
        this.plain = $('.js-plain');
    }

    MaskMoney.prototype.enable = function () {
        this.decimal.maskNumber({decimal: ',', thousands: '.'});
        this.plain.maskNumber({integer: true, thousands: '.'});
    };

    return MaskMoney;

}());

Brewer.MaskPhoneNumber = (function () {

    function MaskPhoneNumber() {
        this.inputPhoneNumber = $('.js-phone-number');
    }

    MaskPhoneNumber.prototype.enable = function () {
        var masKBehavior = function (val) {
            return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000' : '(00) 0000-00009';
        };
        var options = {
            onKeyPress: function (val, e, field, options) {
                field.mask(masKBehavior.apply({}, arguments), options)
            }
        };
        this.inputPhoneNumber.mask(masKBehavior, options);
    };

    return MaskPhoneNumber;

}());

Brewer.MaskCpfCnpjNumber = (function () {

    function MaskCpfCnpjNumber() {
        this.inputCpfCnpjNumber = $('.js-cpf-cnpj');
    }

    MaskCpfCnpjNumber.prototype.enable = function () {

        var maskBehavior = function (val) {
            return val.replace(/\D/g, '').length === 11 ? '000.000.000-00999' : '00.000.000/0000-00';
        };

        var options = {
            clearIfNotMatch: true, // Limpa o campo se não corresponder ao padrão
            placeholder: "CPF:999.999.999-99 / CNPJ:99.999.999/9999-99", // Placeholder
            onKeyPress: function (val, e, field, options) {
                field.mask(maskBehavior.apply({}, arguments), options);
            }
        };

        this.inputCpfCnpjNumber.mask(maskBehavior, options);
    };

    return MaskCpfCnpjNumber;

})();

Brewer.MaskCep = (function () {

    function MaskCep() {
        this.inputCep = $('.js-cep');
    }

    MaskCep.prototype.enable = function () {
        this.inputCep.mask('00.000-000');
    };

    return MaskCep;

}());


Brewer.MaskDate = (function () {

    function MaskDate() {
        this.inputDate = $('.js-date');
    }

    MaskDate.prototype.enable = function () {
        this.inputDate.mask('00/00/0000');
        this.inputDate.datepicker({
            orientation: 'bottom',
            language: 'pt-BR',
            autoclose: 'true'
        });
    };

    return MaskDate;

}());

numeral.language('pt-br');
Brewer.formatarMoeda = function(valor) {
    return numeral(valor).format('0,0.00');
};

Brewer.recuperarValor = function(valorFormatado){
    return numeral().unformat(valorFormatado);
};

Brewer.Security = (function () {

    function Security() {
        this.token = $('input[name=_csrf]').val();
        this.header = $('input[name=_csrf_header]').val();
    }

    Security.prototype.enable = function () {
        $(document).ajaxSend(function (event,jqxhr,settings) {
            jqxhr.setRequestHeader(this.header,this.token)
        }.bind(this));
    };

    return Security;

}());

$(function () {
    var maskMoney = new Brewer.MaskMoney();
    maskMoney.enable();

    var maskPhoneNumber = new Brewer.MaskPhoneNumber();
    maskPhoneNumber.enable();

    var maskCpfCnpj = new Brewer.MaskCpfCnpjNumber();
    //maskCpfCnpj.enable();

    var maskCep = new Brewer.MaskCep();
    maskCep.enable();

    var maskDate = new Brewer.MaskDate();
    maskDate.enable();

    var security = new Brewer.Security();
    security.enable();
});
