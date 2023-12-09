package br.ufrpe.poo.banco.negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.ufrpe.poo.banco.dados.IRepositorioClientes;
import br.ufrpe.poo.banco.dados.IRepositorioContas;
import br.ufrpe.poo.banco.dados.RepositorioClientesArray;
import br.ufrpe.poo.banco.dados.RepositorioContasArquivoBin;
import br.ufrpe.poo.banco.dados.RepositorioContasArray;
import br.ufrpe.poo.banco.exceptions.AtualizacaoNaoRealizadaException;
import br.ufrpe.poo.banco.exceptions.ClienteJaCadastradoException;
import br.ufrpe.poo.banco.exceptions.ClienteJaPossuiContaException;
import br.ufrpe.poo.banco.exceptions.ClienteNaoCadastradoException;
import br.ufrpe.poo.banco.exceptions.ClienteNaoPossuiContaException;
import br.ufrpe.poo.banco.exceptions.ContaJaAssociadaException;
import br.ufrpe.poo.banco.exceptions.ContaJaCadastradaException;
import br.ufrpe.poo.banco.exceptions.ContaNaoEncontradaException;
import br.ufrpe.poo.banco.exceptions.InicializacaoSistemaException;
import br.ufrpe.poo.banco.exceptions.RenderBonusContaEspecialException;
import br.ufrpe.poo.banco.exceptions.RenderJurosPoupancaException;
import br.ufrpe.poo.banco.exceptions.RepositorioException;
import br.ufrpe.poo.banco.exceptions.SaldoInsuficienteException;
import br.ufrpe.poo.banco.exceptions.ValorInvalidoException;

public class TesteBanco {

	private static Banco banco;

	@Before
	public void apagarArquivos() throws IOException, RepositorioException,
			InicializacaoSistemaException {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("clientes.dat"));
		bw.close();
		bw = new BufferedWriter(new FileWriter("contas.dat"));
		bw.close();
		
		Banco.instance = null;
		TesteBanco.banco = Banco.getInstance();
	}

	@Test(expected = ClienteNaoCadastradoException.class)
	public void testeAssociarContaClienteNaoCadastrado() throws ClienteJaPossuiContaException, ContaJaAssociadaException, ClienteNaoCadastradoException, RepositorioException {
	    IRepositorioClientes repositorioClientesMock = mock(IRepositorioClientes.class);
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);

	    when(repositorioClientesMock.procurar(anyString())).thenReturn(null);

	    Banco banco = new Banco(repositorioClientesMock, repositorioContasMock);
	    banco.associarConta("12345678900", "12345");
	}

	@Test
	public void testeAssociarContaClienteCadastradoContaNaoAssociada() throws Exception {
	    IRepositorioClientes repositorioClientesMock = mock(IRepositorioClientes.class);
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    Cliente clienteMock = mock(Cliente.class);

	    when(repositorioClientesMock.procurar("12345678900")).thenReturn(clienteMock);
	    when(repositorioContasMock.procurar("12345")).thenReturn(null);

	    Banco banco = new Banco(repositorioClientesMock, repositorioContasMock);
	    banco.associarConta("12345678900", "12345");

	    verify(clienteMock).adicionarConta("12345");
	    verify(repositorioClientesMock).atualizar(clienteMock);
	}

	@Test(expected = ContaJaAssociadaException.class)
	public void testeAssociarContaClienteCadastradoContaJaAssociada() throws Exception {
	    IRepositorioClientes repositorioClientesMock = mock(IRepositorioClientes.class);
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    Cliente clienteMock = mock(Cliente.class);
	    ContaAbstrata contaMock = mock(ContaAbstrata.class);

	    when(repositorioClientesMock.procurar("12345678900")).thenReturn(clienteMock);
	    when(repositorioContasMock.procurar("12345")).thenReturn(contaMock);

	    Banco banco = new Banco(repositorioClientesMock, repositorioContasMock);
	    banco.associarConta("12345678900", "12345");
	}
	
	@Test
	public void testeAtualizarClienteSucesso() throws RepositorioException, AtualizacaoNaoRealizadaException {
	    IRepositorioClientes repositorioClientesMock = mock(IRepositorioClientes.class);
	    when(repositorioClientesMock.atualizar(any(Cliente.class))).thenReturn(true);

	    Banco banco = new Banco(repositorioClientesMock, null);
	    Cliente cliente = new Cliente("Nome", "CPF");

	    banco.atualizarCliente(cliente);

	    verify(repositorioClientesMock).atualizar(cliente);
	}
	
	@Test(expected = AtualizacaoNaoRealizadaException.class)
	public void testeAtualizarClienteFalha() throws RepositorioException, AtualizacaoNaoRealizadaException {
	    IRepositorioClientes repositorioClientesMock = mock(IRepositorioClientes.class);
	    when(repositorioClientesMock.atualizar(any(Cliente.class))).thenReturn(false);

	    Banco banco = new Banco(repositorioClientesMock, null);
	    Cliente cliente = new Cliente("Nome", "CPF");

	    banco.atualizarCliente(cliente);
	}

	@Test
	public void testeGetInstanceWhenNull() throws Exception {
	    // Usando reflexão para definir Banco.instance como null
	    Field instanceField = Banco.class.getDeclaredField("instance");
	    instanceField.setAccessible(true);
	    instanceField.set(null, null);

	    Banco banco = Banco.getInstance();
	    assertNotNull(banco);
	}
	
	@Test
	public void testeGetInstanceWhenAlreadyInstantiated() throws RepositorioException, InicializacaoSistemaException {
	    // Primeira chamada para criar a instância
	    Banco firstInstance = Banco.getInstance();

	    // Segunda chamada deve retornar a mesma instância
	    Banco secondInstance = Banco.getInstance();

	    assertSame(firstInstance, secondInstance);
	}
	
	@Ignore
	@Test(expected = InicializacaoSistemaException.class)
	public void testGetInstanceWithException() throws Exception {
	    // Reset Banco.instance para null
	    Field instanceField = Banco.class.getDeclaredField("instance");
	    instanceField.setAccessible(true);
	    instanceField.set(null, null);

	    // Criar mocks
	    IRepositorioClientes repoClientesMock = mock(IRepositorioClientes.class);
	    IRepositorioContas repoContasMock = mock(IRepositorioContas.class);

	    // Configurar mock para lançar exceção
	    when(repoClientesMock.inserir(any(Cliente.class))).thenThrow(new RepositorioException("Falha simulada"));

	    // Injetar mocks no Banco usando reflexão
	    Constructor<Banco> constructor = Banco.class.getDeclaredConstructor(IRepositorioClientes.class, IRepositorioContas.class);
	    constructor.setAccessible(true);
	    constructor.newInstance(repoClientesMock, repoContasMock);

	    // Chamar getInstance, esperando que lance InicializacaoSistemaException
	    Banco.getInstance();
	}



	/**
	 * Verifica o cadastramento de uma nova conta.
	 * 
	 */
	@Test
	public void testeCadastarNovaConta() throws RepositorioException,
			ContaJaCadastradaException, ContaNaoEncontradaException,
			InicializacaoSistemaException {

		Banco banco = new Banco(null, new RepositorioContasArquivoBin());
		ContaAbstrata conta1 = new Conta("1", 100);
		banco.cadastrar(conta1);
		ContaAbstrata conta2 = banco.procurarConta("1");
		assertEquals(conta1.getNumero(), conta2.getNumero());
		assertEquals(conta1.getSaldo(), conta2.getSaldo(), 0);
	}
	
	@Test
	public void testeCadastrarClienteSucesso() throws RepositorioException, ClienteJaCadastradoException {
	    IRepositorioClientes repositorioMock = mock(IRepositorioClientes.class);
	    when(repositorioMock.inserir(any(Cliente.class))).thenReturn(true);

	    Banco banco = new Banco(repositorioMock, null);
	    Cliente cliente = new Cliente("Nome", "CPF");

	    banco.cadastrarCliente(cliente);

	    verify(repositorioMock).inserir(cliente);
	}
	
	@Test(expected = ClienteJaCadastradoException.class)
	public void testCadastrarClienteFalha() throws RepositorioException, ClienteJaCadastradoException {
	    IRepositorioClientes repositorioClientesMock = mock(IRepositorioClientes.class);
	    when(repositorioClientesMock.inserir(any(Cliente.class))).thenReturn(false);

	    Banco banco = new Banco(repositorioClientesMock, null);
	    Cliente cliente = new Cliente("Nome", "CPF");

	    banco.cadastrarCliente(cliente);
	}

	/**
	 * Verifica que nao e permitido cadastrar duas contas com o mesmo numero.
	 * 
	 */
	@Test(expected = ContaJaCadastradaException.class)
	public void testeCadastrarContaExistente() throws RepositorioException,
			ContaJaCadastradaException, ContaNaoEncontradaException,
			InicializacaoSistemaException {

		Conta c1 = new Conta("1", 200);
		Conta c2 = new Conta("1", 300);
		banco.cadastrar(c1);
		banco.cadastrar(c2);
		fail("Excecao ContaJaCadastradaException nao levantada");
	}

	/**
	 * Verifica se o credito esta sendo executado corretamente em uma conta
	 * corrente.
	 * 
	 */
	@Test
	public void testeCreditarContaExistente() throws RepositorioException,
			ContaNaoEncontradaException, InicializacaoSistemaException,
			ContaJaCadastradaException, ValorInvalidoException {

		ContaAbstrata conta = new Conta("1", 100);
		banco.cadastrar(conta);
		banco.creditar(conta, 100);
		conta = banco.procurarConta("1");
		assertEquals(200, conta.getSaldo(), 0);
	}

	/**
	 * Verifica a excecao levantada na tentativa de creditar em uma conta que
	 * nao existe.
	 * 
	 */
	@Test(expected = ContaNaoEncontradaException.class)
	public void testeCreditarContaInexistente() throws RepositorioException,
			ContaNaoEncontradaException, InicializacaoSistemaException,
			ValorInvalidoException {

		banco.creditar(new Conta("", 0), 200);

		fail("Excecao ContaNaoEncontradaException nao levantada");
	}
	
	@Test(expected = ValorInvalidoException.class)
	public void testeCreditarValorNegativo() throws RepositorioException, ValorInvalidoException, ContaNaoEncontradaException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    when(repositorioContasMock.existe(anyString())).thenReturn(true);

	    Banco banco = new Banco(null, repositorioContasMock);
	    ContaAbstrata conta = new Conta("12345", 100);

	    banco.creditar(conta, -50);
	}

	/**
	 * Verifica que a operacao de debito em conta corrente esta acontecendo
	 * corretamente.
	 * 
	 */
	@Test
	public void testeDebitarContaExistente() throws RepositorioException,
			ContaNaoEncontradaException, SaldoInsuficienteException,
			InicializacaoSistemaException, ContaJaCadastradaException,
			ValorInvalidoException {

		ContaAbstrata conta = new Conta("1", 50);
		banco.cadastrar(conta);
		banco.debitar(conta, 50);
		conta = banco.procurarConta("1");
		assertEquals(0, conta.getSaldo(), 0);
	}

	/**
	 * Verifica que tentantiva de debitar em uma conta que nao existe levante
	 * excecao.
	 * 
	 */
	@Test(expected = ContaNaoEncontradaException.class)
	public void testeDebitarContaInexistente() throws RepositorioException,
			ContaNaoEncontradaException, SaldoInsuficienteException,
			InicializacaoSistemaException, ValorInvalidoException {

		banco.debitar(new Conta("", 0), 50);
		fail("Excecao ContaNaoEncontradaException nao levantada");
	}
	
	@Test(expected = ValorInvalidoException.class)
	public void testeDebitarValorNegativo() throws RepositorioException, SaldoInsuficienteException, ValorInvalidoException, ContaNaoEncontradaException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    when(repositorioContasMock.existe(anyString())).thenReturn(true);

	    Banco banco = new Banco(null, repositorioContasMock);
	    ContaAbstrata conta = new Conta("12345", 100);

	    banco.debitar(conta, -50);
	}

	/**
	 * Verifica que a transferencia entre contas correntes e realizada com
	 * sucesso.
	 * 
	 */
	@Test
	public void testeTransferirContaExistente() throws RepositorioException,
			ContaNaoEncontradaException, SaldoInsuficienteException,
			InicializacaoSistemaException, ContaJaCadastradaException,
			ValorInvalidoException {

		ContaAbstrata conta1 = new Conta("1", 100);
		ContaAbstrata conta2 = new Conta("2", 200);
		banco.cadastrar(conta1);
		banco.cadastrar(conta2);
		banco.transferir(conta1, conta2, 50);
		conta1 = banco.procurarConta("1");
		conta2 = banco.procurarConta("2");
		assertEquals(50, conta1.getSaldo(), 0);
		assertEquals(250, conta2.getSaldo(), 0);
	}

	/**
	 * Verifica que tentativa de transferir entre contas cujos numeros nao
	 * existe levanta excecao.
	 * 
	 */
	@Test(expected = ContaNaoEncontradaException.class)
	public void testeTransferirContaInexistente() throws RepositorioException, SaldoInsuficienteException, ValorInvalidoException, ContaNaoEncontradaException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    // Configurar para que uma das contas não exista
	    when(repositorioContasMock.existe("contaOrigem")).thenReturn(true);
	    when(repositorioContasMock.existe("contaDestino")).thenReturn(false);

	    Banco banco = new Banco(null, repositorioContasMock);
	    ContaAbstrata contaOrigem = new Conta("contaOrigem", 100);
	    ContaAbstrata contaDestino = new Conta("contaDestino", 50);

	    banco.transferir(contaOrigem, contaDestino, 10);
	}

	@Test(expected = ContaNaoEncontradaException.class)
	public void testeTransferirAmbasContasInexistentes() throws RepositorioException, SaldoInsuficienteException, ValorInvalidoException, ContaNaoEncontradaException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    // Configurar para que ambas as contas não existam
	    when(repositorioContasMock.existe("contaOrigem")).thenReturn(false);
	    when(repositorioContasMock.existe("contaDestino")).thenReturn(false);

	    Banco banco = new Banco(null, repositorioContasMock);
	    ContaAbstrata contaOrigem = new Conta("contaOrigem", 100);
	    ContaAbstrata contaDestino = new Conta("contaDestino", 50);

	    banco.transferir(contaOrigem, contaDestino, 10);
	}

	@Test
	public void testRemoverClienteComContasReais() throws Exception {
	    // Criar repositórios reais
	    IRepositorioClientes repositorioClientes = new RepositorioClientesArray();
	    IRepositorioContas repositorioContas = new RepositorioContasArray();

	    // Criar um banco com os repositórios
	    Banco banco = new Banco(repositorioClientes, repositorioContas);

	    // Criar um cliente e contas reais
	    Cliente cliente = new Cliente("Nome", "12345678900");
	    ContaAbstrata conta1 = new Conta("12345", 100);
	    ContaAbstrata conta2 = new Conta("67890", 200);

	    // Adicionar contas ao cliente e cadastrar cliente e contas
	    cliente.adicionarConta(conta1.getNumero());
	    cliente.adicionarConta(conta2.getNumero());
	    repositorioClientes.inserir(cliente);
	    repositorioContas.inserir(conta1);
	    repositorioContas.inserir(conta2);

	    // Executar o método a ser testado
	    banco.removerCliente(cliente.getCpf());

	    // Verificar se as contas foram removidas
	    assertFalse(repositorioContas.existe(conta1.getNumero()));
	    assertFalse(repositorioContas.existe(conta2.getNumero()));
	    // Verificar se o cliente foi removido
	    assertFalse(repositorioClientes.existe(cliente.getCpf()));
	}

	@Test(expected = ClienteNaoCadastradoException.class)
	public void testRemoverClienteFalha() throws RepositorioException, ClienteNaoCadastradoException, ContaNaoEncontradaException, ClienteNaoPossuiContaException {
	    IRepositorioClientes repositorioClientesMock = mock(IRepositorioClientes.class);
	    Cliente clienteMock = mock(Cliente.class);

	    when(repositorioClientesMock.procurar("12345678900")).thenReturn(clienteMock);
	    when(clienteMock.getContas()).thenReturn(new ArrayList<>());
	    when(repositorioClientesMock.remover("12345678900")).thenReturn(false);

	    Banco banco = new Banco(repositorioClientesMock, null);
	    banco.removerCliente("12345678900");
	}


	@Test
	public void testRemoverContaSucesso() throws RepositorioException, ContaNaoEncontradaException, ClienteNaoPossuiContaException, ClienteJaPossuiContaException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    IRepositorioClientes repositorioClientesMock = mock(IRepositorioClientes.class);
	    when(repositorioContasMock.remover(anyString())).thenReturn(true);

	    Banco banco = new Banco(repositorioClientesMock, repositorioContasMock);
	    Cliente cliente = new Cliente("Nome", "CPF");
	    cliente.adicionarConta("12345");

	    banco.removerConta(cliente, "12345");

	    verify(repositorioContasMock).remover("12345");
	    verify(repositorioClientesMock).atualizar(cliente);
	}

	@Test(expected = ContaNaoEncontradaException.class)
	public void testRemoverContaFalha() throws RepositorioException, ContaNaoEncontradaException, ClienteNaoPossuiContaException, ClienteJaPossuiContaException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    IRepositorioClientes repositorioClientesMock = mock(IRepositorioClientes.class);
	    when(repositorioContasMock.remover(anyString())).thenReturn(false);

	    Banco banco = new Banco(repositorioClientesMock, repositorioContasMock);
	    Cliente cliente = new Cliente("Nome", "CPF");
	    cliente.adicionarConta("12345");

	    banco.removerConta(cliente, "12345");
	}

	/**
	 * Verifica que render juros de uma conta poupanca funciona corretamente
	 * 
	 */
	@Test
	public void testeRenderJurosContaExistente() throws RepositorioException,
			ContaNaoEncontradaException, RenderJurosPoupancaException,
			InicializacaoSistemaException, ContaJaCadastradaException {

		Poupanca poupanca = new Poupanca("20", 100);
		banco.cadastrar(poupanca);
		double saldoSemJuros = poupanca.getSaldo();
		double saldoComJuros = saldoSemJuros + (saldoSemJuros * 0.008);
		poupanca.renderJuros(0.008);
		assertEquals(saldoComJuros, poupanca.getSaldo(), 0);
	}

	/**
	 * Verifica que tentativa de render juros em conta inexistente levanta
	 * excecao.
	 * 
	 */
	@Test(expected = ContaNaoEncontradaException.class)
	public void testeRenderJurosContaInexistente() throws RepositorioException, ContaNaoEncontradaException, RenderJurosPoupancaException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    when(repositorioContasMock.existe(anyString())).thenReturn(false);

	    Banco banco = new Banco(null, repositorioContasMock);
	    Poupanca poupanca = new Poupanca("inexistente", 100);
	    banco.renderJuros(poupanca);
	}


	/**
	 * Verifica que tentativa de render juros em conta que nao e poupanca
	 * levanta excecao.
	 * 
	 */
	@Test(expected = RenderJurosPoupancaException.class)
	public void testeRenderJurosContaNaoEhPoupanca() throws RepositorioException, ContaNaoEncontradaException, RenderJurosPoupancaException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    when(repositorioContasMock.existe(anyString())).thenReturn(true);

	    Banco banco = new Banco(null, repositorioContasMock);
	    ContaAbstrata contaNaoPoupanca = new Conta("12345", 100);
	    banco.renderJuros(contaNaoPoupanca);
	}

	@Test
	public void testeRenderJurosPoupancaExistente() throws RepositorioException, ContaNaoEncontradaException, RenderJurosPoupancaException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    when(repositorioContasMock.existe(anyString())).thenReturn(true);
	    when(repositorioContasMock.atualizar(any(ContaAbstrata.class))).thenReturn(true);

	    Banco banco = new Banco(null, repositorioContasMock);
	    Poupanca poupanca = new Poupanca("existente", 100);

	    // Executar o método renderJuros
	    banco.renderJuros(poupanca);

	    // Verificar se o saldo foi atualizado corretamente após renderizar os juros
	    double taxaDeJuros = 0.5; // Supondo que 0.5 seja a taxa de juros
	    double jurosEsperados = 100 * taxaDeJuros;
	    assertEquals(100 + jurosEsperados, poupanca.getSaldo(), 0);
	}

	@Test(expected = ContaNaoEncontradaException.class)
	public void testeRenderJurosPoupancaInexistente() throws RepositorioException, ContaNaoEncontradaException, RenderJurosPoupancaException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    when(repositorioContasMock.existe(anyString())).thenReturn(false);

	    Banco banco = new Banco(null, repositorioContasMock);
	    Poupanca poupanca = new Poupanca("inexistente", 100);
	    banco.renderJuros(poupanca);
	}

	/**
	 * Verifica que render bonus de uma conta especial funciona corretamente.
	 * 
	 */
	@Test
	public void testeRenderBonusContaEspecialExistente() throws RepositorioException, ContaNaoEncontradaException, RenderBonusContaEspecialException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    when(repositorioContasMock.existe(anyString())).thenReturn(true);
	    when(repositorioContasMock.atualizar(any(ContaAbstrata.class))).thenReturn(true);

	    Banco banco = new Banco(null, repositorioContasMock);
	    ContaEspecial contaEspecial = new ContaEspecial("12345", 100);

	    // Creditar um valor para acumular bônus
	    double valorCreditado = 1000;
	    contaEspecial.creditar(valorCreditado);
	    double bonusEsperado = valorCreditado * 0.01;

	    // Renderizar o bônus
	    banco.renderBonus(contaEspecial);

	    // Verificar se o bônus foi creditado corretamente
	    assertEquals(100 + valorCreditado + bonusEsperado, contaEspecial.getSaldo(), 0);
	    // Verificar se o bônus foi zerado
	    assertEquals(0, contaEspecial.getBonus(), 0);
	}


	/**
	 * Verifica que a tentativa de render bonus em inexistente levanta excecao.
	 * 
	 */
	@Test(expected = ContaNaoEncontradaException.class)
	public void testeRenderBonusContaEspecialNaoInexistente() throws RepositorioException, ContaNaoEncontradaException, RenderBonusContaEspecialException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    when(repositorioContasMock.existe(anyString())).thenReturn(false);

	    Banco banco = new Banco(null, repositorioContasMock);
	    ContaEspecial contaEspecial = new ContaEspecial("inexistente", 100);
	    banco.renderBonus(contaEspecial);
	}


	/**
	 * Verifica que tentativa de render bonus em conta que nao e especial
	 * levante excecao.
	 */
	
	@Test(expected = RenderBonusContaEspecialException.class)
	public void testeRenderBonusContaNaoEspecial() throws RepositorioException, ContaNaoEncontradaException, RenderBonusContaEspecialException {
	    IRepositorioContas repositorioContasMock = mock(IRepositorioContas.class);
	    when(repositorioContasMock.existe(anyString())).thenReturn(true);

	    Banco banco = new Banco(null, repositorioContasMock);
	    ContaAbstrata contaNaoEspecial = new Conta("12345", 100);
	    banco.renderBonus(contaNaoEspecial);
	}

}
