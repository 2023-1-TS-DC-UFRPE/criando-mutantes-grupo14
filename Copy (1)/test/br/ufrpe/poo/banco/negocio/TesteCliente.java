package br.ufrpe.poo.banco.negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import br.ufrpe.poo.banco.exceptions.ClienteJaPossuiContaException;
import br.ufrpe.poo.banco.exceptions.ClienteNaoPossuiContaException;

/**
 * Classe de teste respons�vel por testar as condi��es dos m�todos
 * adicionarConta e removerConta da classe Cliente.
 * 
 * @author Aluno
 * 
 */
public class TesteCliente {

	/**
	 * Testa a inser��o de uma nova conta vinculada ao cliente
	 */
	@Test
	public void adicionarContaTest() {
		Cliente c1 = new Cliente("nome", "123");
		try {
			c1.adicionarConta("1");
		} catch (ClienteJaPossuiContaException e) {
			fail();
		}
		assertEquals(c1.procurarConta("1"), 0);
	}

	/**
	 * Testa a condi��o da tentativa de adicionar uma conta j� existente � lista
	 * de contas do cliente
	 * 
	 * @throws ClienteJaPossuiContaException
	 */
	@Test(expected = ClienteJaPossuiContaException.class)
	public void adicionarContaJaExistenteTest()
			throws ClienteJaPossuiContaException {
		Cliente c1 = new Cliente("nome", "123");
		c1.adicionarConta("1"); // adiciona a conta a 1� vez
		c1.adicionarConta("1"); // tentativa de adicionar a mesma conta
								// novamente
	}

	/**
	 * Teste a remo��o de uma conta da lista de contas do cliente
	 */
	@Test
	public void removerContaClienteTest() {
		Cliente c1 = new Cliente("nome", "123");
		try {
			c1.adicionarConta("1"); // adiciona conta com n�mero 1
			c1.removerConta("1"); // remove a conta de n�mero 1
		} catch (Exception e) {
			fail("Exce��o inesperada lancada!");
		}

		assertEquals(c1.procurarConta("1"), -1);
	}

	/**
	 * Testa a remo��o de uma determinada conta que n�o est� vinculada ao
	 * cliente
	 * 
	 * @throws ClienteNaoPossuiContaException
	 */
	@Test(expected = ClienteNaoPossuiContaException.class)
	public void removerContaClienteSemContaTest()
			throws ClienteNaoPossuiContaException {
		Cliente c1 = new Cliente("nome", "123");
		c1.removerConta("1"); // tenta remover a conta de um cliente sem contas
	}
	
	@Test
	public void testEqualsWithSameCpf() {
	    Cliente cliente1 = new Cliente("Nome1", "123");
	    Cliente cliente2 = new Cliente("Nome2", "123");

	    assertTrue(cliente1.equals(cliente2));
	}
	
	@Test
	public void testEqualsWithDifferentCpf() {
	    Cliente cliente1 = new Cliente("Nome1", "123");
	    Cliente cliente2 = new Cliente("Nome2", "456");

	    assertFalse(cliente1.equals(cliente2));
	}
	
	@Test
	public void testEqualsWithNonClienteObject() {
	    Cliente cliente = new Cliente("Nome", "123");
	    Object outroObjeto = new Object();

	    assertFalse(cliente.equals(outroObjeto));
	}

	@Test
	public void testEqualsWithSameObject() {
	    Cliente cliente = new Cliente("Nome", "123");

	    assertTrue(cliente.equals(cliente));
	}
	
	@Test
	public void testEqualsWithNullObject() {
	    Cliente cliente = new Cliente("Nome", "123");

	    assertFalse(cliente.equals(null));
	}

	@Test
	public void testGetNome() {
	    Cliente cliente = new Cliente("Nome Teste", "123456789");
	    assertEquals("Nome Teste", cliente.getNome());
	}
	
	@Test
	public void testToString() throws ClienteJaPossuiContaException {
	    Cliente cliente = new Cliente("Nome Teste", "123456789");
	    cliente.adicionarConta("123");
	    cliente.adicionarConta("456");

	    String expected = "Nome: Nome Teste\nCPF: 123456789\nContas: [123, 456]";
	    assertEquals(expected, cliente.toString());
	}
	
	@Test
	public void testGetContasVazio() {
	    Cliente cliente = new Cliente("Nome Teste", "123456789");
	    assertTrue("Lista de contas deve estar vazia inicialmente", cliente.getContas().isEmpty());
	}

	@Test
	public void testGetContasComUmaConta() throws ClienteJaPossuiContaException {
	    Cliente cliente = new Cliente("Nome Teste", "123456789");
	    cliente.adicionarConta("123");
	    assertEquals("Lista de contas deve conter uma conta", 1, cliente.getContas().size());
	    assertTrue("Lista de contas deve conter a conta '123'", cliente.getContas().contains("123"));
	}

	@Test
	public void testGetContasComMultiplasContas() throws ClienteJaPossuiContaException {
	    Cliente cliente = new Cliente("Nome Teste", "123456789");
	    cliente.adicionarConta("123");
	    cliente.adicionarConta("456");
	    assertEquals("Lista de contas deve conter duas contas", 2, cliente.getContas().size());
	    assertTrue("Lista de contas deve conter a conta '123'", cliente.getContas().contains("123"));
	    assertTrue("Lista de contas deve conter a conta '456'", cliente.getContas().contains("456"));
	}

	@Test
	public void testConsultarNumeroContaValido() throws ClienteJaPossuiContaException {
	    Cliente cliente = new Cliente("Nome Teste", "123456789");
	    cliente.adicionarConta("123");
	    cliente.adicionarConta("456");
	    assertEquals("Deve retornar a conta '123'", "123", cliente.consultarNumeroConta(0));
	    assertEquals("Deve retornar a conta '456'", "456", cliente.consultarNumeroConta(1));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testConsultarNumeroContaIndiceInvalido() {
	    Cliente cliente = new Cliente("Nome Teste", "123456789");
	    cliente.consultarNumeroConta(0); // Lança exceção, pois não há contas adicionadas.
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testConsultarNumeroContaIndiceNegativo() throws ClienteJaPossuiContaException {
	    Cliente cliente = new Cliente("Nome Teste", "123456789");
	    cliente.adicionarConta("123");
	    cliente.consultarNumeroConta(-1); // Lança exceção, pois o índice é negativo.
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testConsultarNumeroContaIndiceExcedente() throws ClienteJaPossuiContaException {
	    Cliente cliente = new Cliente("Nome Teste", "123456789");
	    cliente.adicionarConta("123");
	    cliente.consultarNumeroConta(1); // Lança exceção, pois o índice excede o tamanho da lista.
	}

	@Test
	public void testRemoverTodasAsContasComContas() throws ClienteJaPossuiContaException {
	    Cliente cliente = new Cliente("Nome Teste", "123456789");
	    cliente.adicionarConta("123");
	    cliente.adicionarConta("456");
	    cliente.removerTodasAsContas();
	    assertNull("Lista de contas deve ser null após remover todas as contas", cliente.getContas());
	}

	@Test
	public void testRemoverTodasAsContasSemContas() {
	    Cliente cliente = new Cliente("Nome Teste", "123456789");
	    cliente.removerTodasAsContas();
	    assertNull("Lista de contas deve ser null se não houver contas para remover", cliente.getContas());
	}
	@Test
	public void testSetNome() {
	    Cliente cliente = new Cliente("Nome Antigo", "123456789");
	    cliente.setNome("Nome Novo");
	    assertEquals("O nome do cliente deve ser atualizado para 'Nome Novo'", "Nome Novo", cliente.getNome());
	}

	@Test
	public void testSetCpf() {
	    Cliente cliente = new Cliente("Nome Teste", "123456789");
	    cliente.setCpf("987654321");
	    assertEquals("O CPF do cliente deve ser atualizado para '987654321'", "987654321", cliente.getCpf());
	}

}
