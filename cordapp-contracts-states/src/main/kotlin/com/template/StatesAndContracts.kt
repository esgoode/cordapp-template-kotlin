package com.template

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.identity.Party

// ************
// * Contract *
// ************
class CashIssuance : Contract {

    class Create : CommandData
    
    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Create>()

        requireThat{
            "No inputs consumed when issuing cash" using (tx.inputs.isEmpty())
            "One output state" using (tx.outputs.size == 1)

            val out = tx.outputsOfType<MintState>().single()
            "Cannot issue negative cash" using (out.value > 0)

            "Issuer must sign" using (command.signers.toSet().zie == 1)
            "Lender must sign" using (command.signers.containsAll(listOf(out.lender.owningKey)))
        }
    }
}

// class UnderwritedDebtIssuance : Contract {
//     companion object {
//         // Used to identify our contract when building a transaction.
//         const val ID = "com.template.TemplateContract"
//     }
    
//     // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
//     // does not throw an exception.
//     override fun verify(tx: LedgerTransaction) {
//         // Verification logic goes here.
//     }

//     // Used to indicate the transaction's intent.
//     interface Commands : CommandData {
//         class Action : Commands
//     }
// }

// class DebtIssuance : Contract {
//     companion object {
//         // Used to identify our contract when building a transaction.
//         const val ID = "com.template.TemplateContract"
//     }
    
//     // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
//     // does not throw an exception.
//     override fun verify(tx: LedgerTransaction) {
//         // Verification logic goes here.
//     }

//     // Used to indicate the transaction's intent.
//     interface Commands : CommandData {
//         class Action : Commands
//     }
// }

// *********
// * State *
// *********
class MintState(val value: Int,
               val lender: Party,
               val borrower: Party) : ContractState {
    override val participants get() = listOf(lender, borrower)
}