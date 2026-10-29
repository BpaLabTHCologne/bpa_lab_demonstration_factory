package de.thkoeln.bpalab.fakefactory.controller

import de.thkoeln.bpalab.fakefactory.model.TopicPayloadDTO
import de.thkoeln.bpalab.fakefactory.service.MqttService
import de.thkoeln.bpalab.fakefactory.service.TopicPayloadService
import de.thkoeln.bpalab.fakefactory.util.WebUtils
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import org.springframework.web.servlet.mvc.support.RedirectAttributes


@Controller
@RequestMapping("/topicPayloads")
class TopicPayloadController(
    private val topicPayloadService: TopicPayloadService,
    private val mqttService: MqttService
) {

    @GetMapping
    fun list(model: Model): String {
        model.addAttribute("topicPayloads", topicPayloadService.findAll())
        return "topicPayload/list"
    }

    @GetMapping("/add")
    fun add(@ModelAttribute("topicPayload") topicPayloadDTO: TopicPayloadDTO): String =
            "topicPayload/add"

    @PostMapping("/add")
    fun add(
        @ModelAttribute("topicPayload") @Valid topicPayloadDTO: TopicPayloadDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            return "topicPayload/add"
        }
        topicPayloadService.create(topicPayloadDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("topicPayload.create.success"))
        return "redirect:/topicPayloads"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable(name = "id") id: Long, model: Model): String {
        model.addAttribute("topicPayload", topicPayloadService.get(id))
        return "topicPayload/edit"
    }

    @PostMapping("/edit/{id}")
    fun edit(
        @PathVariable(name = "id") id: Long,
        @ModelAttribute("topicPayload") @Valid topicPayloadDTO: TopicPayloadDTO,
        bindingResult: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            return "topicPayload/edit"
        }
        topicPayloadService.update(id, topicPayloadDTO)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS,
                WebUtils.getMessage("topicPayload.update.success"))
        return "redirect:/topicPayloads"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable(name = "id") id: Long, redirectAttributes: RedirectAttributes):
            String {
        topicPayloadService.delete(id)
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO,
                WebUtils.getMessage("topicPayload.delete.success"))
        return "redirect:/topicPayloads"
    }
}

@RestController
@RequestMapping("/topicPayloads")
class StreamController(
    private val topicPayloadService: TopicPayloadService,
    private val mqttService: MqttService
) {
    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun stream(): SseEmitter = mqttService.createEmitter()

    @GetMapping("/send/{id}")
    fun send(@PathVariable id: Long): ResponseEntity<String> {
        val topicPayloadDTO = topicPayloadService.get(id)
        topicPayloadService.update(id, topicPayloadDTO);
        mqttService.publishMessage(topicPayloadDTO)
        return ResponseEntity.ok<String>("TopicPayload with id " + id + " sent successfully")
    }

    @PostMapping( "/editr/{id}")
    fun editTopicPayload(@PathVariable id: Long, @RequestBody topicPayloadDTO: TopicPayloadDTO): ResponseEntity<TopicPayloadDTO>  {
        println("TopicPayload id $id update")
        topicPayloadService.update(id, topicPayloadDTO)
        println("TopicPayload " + topicPayloadDTO.topic + ": " + topicPayloadDTO.payload + " has been updated")
        return ResponseEntity.ok<TopicPayloadDTO>(topicPayloadDTO);
    }
}