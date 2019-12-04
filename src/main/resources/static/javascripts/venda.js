Brewer.Venda = (function () {

    function Venda(tabelaItens) {
        this.tabelaItens = tabelaItens;
        this.valorTotalBox = $('.js-valor-total-box');
        this.valorFreteInput = $('#valorFrete');
        this.valorDescontoInput = $('#valorDesconto');

        this.valorTotalItens = (this.tabelaItens.valorTotal() || 0);
        this.valorFrete = (this.valorFreteInput.data('valor')  || 0);
        this.valorDesconto = (this.valorDescontoInput.data('valor') || 0);
    }

    Venda.prototype.iniciar = function () {
        this.tabelaItens.on('tabela-itens-atualizada', onTabelaItensAtualizada.bind(this));
        this.valorFreteInput.on('keyup', onValorFreteAlterado.bind(this));
        this.valorDescontoInput.on('keyup', onValorDescontoAlterado.bind(this));

        this.tabelaItens.on('tabela-itens-atualizada', onValoresAlterados.bind(this));
        this.valorFreteInput.on('keyup', onValoresAlterados.bind(this));
        this.valorDescontoInput.on('keyup', onValoresAlterados.bind(this));

        onValoresAlterados.call(this);
    };

    function onTabelaItensAtualizada(event, valorTotalItens) {
        this.valorTotalItens = valorTotalItens == null ? 0 : valorTotalItens;
    }

    function onValorFreteAlterado(event) {
        this.valorFrete = Brewer.recuperarValor($(event.target).val());
    }

    function onValorDescontoAlterado(event) {
        this.valorDesconto = Brewer.recuperarValor($(event.target).val());
    }

    function onValoresAlterados() {
        console.log(this.valorTotalItens);
        console.log(this.valorFrete);
        console.log(this.valorDesconto);
        var valorTotal =  numeral(this.valorTotalItens) + numeral(this.valorFrete) - numeral(this.valorDesconto);

        console.log(valorTotal);
        this.valorTotalBox.html(Brewer.formatarMoeda(valorTotal));

        $('.js-container-box-total').toggleClass('negativo', valorTotal < 0);
    }
    return Venda;

}());

$(function () {
    var autocomplete = new Brewer.Autocomplete();
    autocomplete.iniciar();

    var tabelaItens = new Brewer.TabelaItens(autocomplete);
    tabelaItens.iniciar();

    var venda = new Brewer.Venda(tabelaItens);
    venda.iniciar();
});