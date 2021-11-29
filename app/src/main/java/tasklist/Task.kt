package tasklist

data class Task(
    val id: String,
    val title: String,
    val description: String = "Ceci est une tâche",

){
}
