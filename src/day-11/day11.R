# халтурим и переписываем все значения из файла вручную
testedInp <- list(list(items=c(79, 98), op=list("multiply", 19), after=list(23, 2, 3)),
               list(items=c(54, 65, 75, 74), op=list("plus", 6), after=list(19, 2, 0)),
               list(items=c(79, 60, 97), op=list("square"), after=list(13, 1, 3)),
               list(items=c(74), op=list("plus", 3), after=list(17, 0, 1)))

realInp <- list(list(items=c(65, 78), op=list("multiply", 3), after=list(5, 2, 3)),
            list(items=c(54, 78, 86, 79, 73, 64, 85, 88), op=list("plus", 8), after=list(11, 4, 7)),
            list(items=c(69, 97, 77, 88, 87), op=list("plus", 2), after=list(2, 5, 3)),
            list(items=c(99), op=list("plus", 4), after=list(13, 1, 5)),
            list(items=c(60, 57, 52), op=list("multiply", 19), after=list(7, 7, 6)),
            list(items=c(91, 82, 85, 73, 84, 53), op=list("plus", 5), after=list(3, 4, 1)),
            list(items=c(88, 74, 68, 56), op=list("square"), after=list(17, 0, 2)),
            list(items=c(54, 82, 72, 71, 53, 99, 67), op=list("plus", 1), after=list(19, 6, 0)))

inp <- realInp

playGame <- function (iterations, input, isPart2 = FALSE) {
    actions <- rep(0, times=length(input)) # тут считаем инспекции для каждой обезьянки
    superMod <- prod(sapply(input, \(x) as.double(x$after[[1]]))) # Супер-модуль — наименьшее общее кратное
                                                                # Если супер-модуль, например, 2*3*5 = 30,
                                                                # уровень переживания, например, 35, 
                                                                # то результат проверки на кратность на 2, 3, 5 
                                                                # для 35 % 30 = 5 такой же, как и для 35:
                                                                # 35 % 2 = 1    5 % 2 = 1
                                                                # 35 % 3 = 2    5 % 3 = 2
                                                                # 35 % 5 = 0    5 % 5 = 0
                                                                # За математическими доказательствами, что это всегда так,
                                                                # я отправлю к математикам

    for (i in seq_len(iterations)) { # цикл по количеству шагов
        for (mIdx in seq_along(input)) { # цикл по обезьянкам
            m <- input[[mIdx]] # обезьянка
            items <- m$items # а это ее вещи
            if (length(items) == 0) next
            items <- items %% superMod # избавляемся от слишком большого числа, если такое получилось

            # снова поленилась и написала вручную 
            if(m$op[1] == "multiply") {
                items <- items * as.integer(m$op[2])
            }
            if(m$op[1] == "plus") {
                items <- items + as.integer(m$op[2])
            }
            if(m$op[1] == "square") {
                items <- items * items
            }

            if (!isPart2) items <- items %/% 3 # целочисленное деление на три
            
            for (item in items) {
                if(item == 0) next
                if (item %% as.integer(m$after[1]) == 0) { # перекладываем вещь к другой обезьянке
                    nextMonkey <- as.integer(m$after[2]) + 1 # нумерация в R начинается с 1
                    input[nextMonkey][[1]]$items <- c(input[nextMonkey][[1]]$items, item)
                } else {
                    nextMonkey <- as.integer(m$after[3]) + 1
                    input[nextMonkey][[1]]$items <- c(input[nextMonkey][[1]]$items, item)
                }
            }
            input[mIdx][[1]]$items <- c() # обнуляем вещи текущей обезьянке
            actions[mIdx] <- actions[mIdx] + length(items) # увеличиваем количество операций у обезьянки
        }   
    }
    top2 <- head(sort(actions, decreasing=TRUE), 2) # выбираем две самые активные обезьянки
    print(prod(top2)) # и перемножаем
}

playGame(20, inp)
playGame(10000, inp, TRUE)

