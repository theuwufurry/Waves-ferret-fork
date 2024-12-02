package gg.aquatic.waves.util.requirement

import gg.aquatic.aquaticseries.lib.requirement.AbstractRequirement
import gg.aquatic.aquaticseries.lib.util.argument.AquaticObjectArgument
import gg.aquatic.aquaticseries.lib.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.aquaticseries.lib.util.updatePAPIPlaceholders
import org.bukkit.entity.Player
import javax.script.ScriptEngineManager

class ExpressionPlayerRequirement: AbstractRequirement<Player>() {
    override fun check(
        binder: Player,
        arguments: Map<String, Any?>
    ): Boolean {
        val expression = arguments["expression"] as String
        return evaluateExpression(expression,binder)
    }

    override fun arguments(): List<AquaticObjectArgument<*>> {
        return listOf(
            PrimitiveObjectArgument("expression", "", true),
        )
    }

    companion object {
        fun evaluateExpression(expression: String, player: Player): Boolean {
            // Replace the placeholder in the expression with the actual value
            val preparedExpression = expression.updatePAPIPlaceholders(player)

            // Use ScriptEngineManager to evaluate the expression
            val engine = ScriptEngineManager().getEngineByName("kotlin")
            return engine.eval(preparedExpression) as Boolean
        }
    }
}