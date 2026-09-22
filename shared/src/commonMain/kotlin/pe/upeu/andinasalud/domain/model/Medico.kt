package pe.upeu.andinasalud.domain.model

data class Medico(val id: String, val nombre: String, val especialidad: String, val sedes: List<Sede>)
