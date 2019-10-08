package ru.digitalhospital.dhdatastorage.controller

//import org.springframework.web.bind.annotation.*
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResource
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.digitalhospital.dhdatastorage.service.ResourceService
import ru.digitalhospital.dhdatastorage.utils.IcdScriptsConverter


/**
 * TODO delete
 * Created at 27.09.2019 16:44 by SherbakovaMA
 *
 * Контроллер для ресурсов
 */
//@RestController
//@RequestMapping("/api/resource")
class ResourcesController(
        private val resourceService: ResourceService
) {

    /**
     * see [ResourceService.byId]
     */
  //  @GetMapping("/{resourceType}/{id}")
    fun readById(
            /*@PathVariable */resourceType: String,
            /*@PathVariable */id: String
    ) = resourceService.byId(resourceType, id)

    /**
     * see [ResourceService.all]
     */
//    @GetMapping("/{resourceType}")
    fun all(
            /*@PathVariable */resourceType: String,
            /*@RequestBody */requestBody: RequestBodyForResources
    ) = resourceService.all(resourceType, requestBody)

    /**
     * see [ResourceService.create]
     */
    /**
     * Создание
     */
    //@PostMapping("create")
    fun create(
            /*@RequestBody */body: RequestBodyForResource
    ) = resourceService.create(body.resource)

    /**
     * see [ResourceService.update]
     */
    //@PutMapping("update")
    fun update(
            /*@RequestBody */body: RequestBodyForResource
    ) = resourceService.update(body.resource)

    /**
     * see [ResourceService.deleteById]
     */
    //@DeleteMapping("/{resourceType}/{id}")
    fun deleteById(
            /*@PathVariable */resourceType: String,
            /*@PathVariable */id: String
    ) = resourceService.deleteById(resourceType, id)

    /**
     * see [ResourceService.deleteAll]
     */
   // @DeleteMapping("/{resourceType}")
    fun deleteAll(
            /*@PathVariable */resourceType: String,
            /*@RequestBody */requestBody: RequestBodyForResources
    ) = resourceService.deleteAll(resourceType, requestBody)

    /**
     * see [IcdScriptsConverter]
     */
    //@PutMapping("convertIcdScripts")
    fun convertIcdScripts() = IcdScriptsConverter().convert()
}