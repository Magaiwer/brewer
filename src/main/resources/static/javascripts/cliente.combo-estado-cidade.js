var Brewer = Brewer || {};

Brewer.ComboEstado = (function () {

    function ComboEstado() {
        this.comboEstado = $('#estado');
        this.emitter = $({});
        this.on = this.emitter.on.bind(this.emitter);
    }

    ComboEstado.prototype.iniciar = function () {
        this.comboEstado.on('change', onEstadoAlterado.bind(this));
    };

    function onEstadoAlterado() {
        this.emitter.trigger('alterado', this.comboEstado.val());
    }

    return ComboEstado;

}());

Brewer.ComboCidade = (function () {

    function ComboCidade(comboEstado) {
        this.comboEstado = comboEstado;
        this.comboCidade = $('#cidade');
        this.imgLoading = $('.js-img-loading');
        this.inputHiddenCidadeSelecionada = $('#inputHiddenCidadeSelecionada');
    }

    ComboCidade.prototype.iniciar = function () {
        reset.call(this);
        this.comboEstado.on('alterado', onEstadoAlterado.bind(this));
        var codigoEstado = this.comboEstado.comboEstado.val();
        inicializarCidades.call(this, codigoEstado);
    };

    function onEstadoAlterado(event, codigoEstado) {
        this.inputHiddenCidadeSelecionada.val('');
        inicializarCidades.call(this,codigoEstado);
    }

    function inicializarCidades(codigoEstado) {
        if (codigoEstado) {
            var resposta = $.ajax({
                url: this.comboCidade.data('url'),
                method: 'GET',
                contentType: 'application/json',
                data: {'estado': codigoEstado},
                beforeSend: iniciarRequisicao.bind(this),
                complete: finalizarRequisicao.bind(this)
            });
            resposta.done(onBuscarCidadeFinalizado.bind(this));
        } else {
            reset.call(this);
        }
    }

    function onBuscarCidadeFinalizado(cidades) {
        var options = [];
        cidades.forEach(function (cidade) {
            options.push('<option value="' + cidade.codigo + '">' + cidade.nome + '</option>');
        });

        this.comboCidade.html(options.join(''));
        this.comboCidade.removeAttr('disabled');

        var codigoCidadeSelecionada = this.inputHiddenCidadeSelecionada.val();
        if (codigoCidadeSelecionada) {
            this.comboCidade.val(codigoCidadeSelecionada);
        }

    }

    function reset() {
        this.comboCidade.html('<option value="">Selecione a cidade</option>');
        this.comboCidade.val('');
        this.comboCidade.attr('disabled', 'disabled');
    }

    function iniciarRequisicao() {
        reset.call(this);
        this.imgLoading.show();
    }

    function finalizarRequisicao() {
        this.imgLoading.hide();

    }

    return ComboCidade;

}());

$(function () {
    var comboEstado = new Brewer.ComboEstado();
    comboEstado.iniciar();

    var comboCidade = new Brewer.ComboCidade(comboEstado);
    comboCidade.iniciar();
});