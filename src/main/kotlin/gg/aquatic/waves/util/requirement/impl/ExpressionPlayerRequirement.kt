package gg.aquatic.waves.util.requirement.impl

import gg.aquatic.waves.util.argument.AquaticObjectArgument
import gg.aquatic.waves.util.argument.impl.PrimitiveObjectArgument
import gg.aquatic.waves.util.requirement.AbstractRequirement
import gg.aquatic.waves.util.updatePAPIPlaceholders
import org.bukkit.entity.Player
import javax.script.ScriptEngineManager

class ExpressionPlayerRequirement : AbstractRequirement<Player>() {
    companion object {
        fun evaluateExpression(expression: String, player: Player): Boolean {
            // Replace the placeholder in the expression with the actual value
            val preparedExpression = expression.updatePAPIPlaceholders(player)

            // Use ScriptEngineManager to evaluate the expression
            val engine = ScriptEngineManager().getEngineByName("kotlin")
            return engine.eval(preparedExpression) as Boolean
        }
    }

    override fun execute(binder: Player, args: Map<String, Any?>, textUpdater: (Player, String) -> String): Boolean {
        val expression = args["expression"] as String
        return evaluateExpression(expression, binder)
    }

    override val arguments: List<AquaticObjectArgument<*>> = listOf(
        PrimitiveObjectArgument("expression", "", true),
    )
}